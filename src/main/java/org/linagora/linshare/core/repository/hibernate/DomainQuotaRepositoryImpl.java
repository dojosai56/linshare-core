/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.repository.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.linagora.linshare.core.domain.constants.QuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.repository.DomainQuotaRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DomainQuotaRepositoryImpl extends GenericQuotaRepositoryImpl<DomainQuota>
		implements DomainQuotaRepository {

	public DomainQuotaRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public DomainQuota find(AbstractDomain domain) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("domain", domain));
		DomainQuota quota = DataAccessUtils.singleResult(findByCriteria(criteria));
		if (quota != null) {
			this.getHibernateTemplate().refresh(quota);
		}
		return quota;
	}

	@Override
	public Long sumOfCurrentValueForSubdomains(AbstractDomain domain) {
		return sumOfCurrentValueForAllMySubdomains(domain) + sumOfcurrentValueForSubdomainsOfAllMySubdomains(domain);
	}

	private Long sumOfCurrentValueForAllMySubdomains(AbstractDomain domain) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("parentDomain", domain));
		criteria.setProjection(Projections.sum("currentValue"));
		List<DomainQuota> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null) {
			return DataAccessUtils.longResult(findByCriteria(criteria));
		}
		return 0L;
	}

	private Long sumOfcurrentValueForSubdomainsOfAllMySubdomains(AbstractDomain domain) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("parentDomain", domain));
		criteria.setProjection(Projections.sum("currentValueForSubdomains"));
		List<DomainQuota> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null) {
			return DataAccessUtils.longResult(findByCriteria(criteria));
		}
		return 0L;
	}

	@Override
	public  Long cascadeMaintenanceMode(AbstractDomain domain, boolean maintenance) {
		HibernateCallback<Long> action = null;
		if (domain.isRootDomain()) {
			action = new HibernateCallback<Long>() {
				public Long doInHibernate(final Session session)
						throws HibernateException, SQLException {
					final Query query = session.createQuery("UPDATE Quota SET maintenance = :maintenance");
					query.setParameter("maintenance", maintenance);
					return (long) query.executeUpdate();
				}
			};
		} else {
			action = new HibernateCallback<Long>() {
				public Long doInHibernate(final Session session)
						throws HibernateException, SQLException {
					final Query query = session.createQuery("UPDATE Quota SET maintenance = :maintenance WHERE domain = :domain OR parentDomain = :domain");
					query.setParameter("domain", domain);
					query.setParameter("maintenance", maintenance);
					return (long) query.executeUpdate();
				}
			};
		}
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(updatedCounter + " Quota have been updated.");
		return updatedCounter;
	}

	@Override
	public Long cascadeDefaultQuota(AbstractDomain domain, Long quota) {
		// update quota of children
		Long count = 0L;
		count += cascadeDefaultQuotaToQuotaOfChildrenDomains(domain, quota);
		// update default quota of children
		count += cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(domain, quota);
		// update default quota of children of children if exists
		// TODO manage sub domains
		List<Long> quotaIdList = getQuotaIdforDefaultQuotaInSubDomains(domain, quota, QuotaType.DOMAIN_QUOTA);
		count += cascadeDefaultQuotaToSubDomainsDefaultQuota(domain, quota, quotaIdList);
		quotaIdList = getQuotaIdforQuotaInSubDomains(domain, quota, QuotaType.DOMAIN_QUOTA);
		count += cascadeDefaultQuotaToSubDomainsQuota(domain, quota, quotaIdList);
		return count;
	}

	public Long cascadeDefaultQuotaToQuotaOfChildrenDomains(AbstractDomain domain, Long quota) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE DomainQuota SET quota = :quota WHERE parentDomain = :parentDomain AND quotaOverride = false");
				query.setParameter("quota", quota);
				query.setParameter("parentDomain", domain);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of DomainQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	public Long cascadeDefaultQuotaToDefaultQuotaOfChildrenDomains(AbstractDomain domain, Long quota) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE DomainQuota SET defaultQuota = :defaultQuota WHERE parentDomain = :parentDomain AND defaultQuotaOverride = false");
				query.setParameter("defaultQuota", quota);
				query.setParameter("parentDomain", domain);
				return (long) query.executeUpdate();
			}
		};
		Long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of DomainQuota have been updated.", updatedCounter);
		return updatedCounter;
	}

	public List<Long> getQuotaIdforDefaultQuotaInSubDomains(AbstractDomain domain, Long quota, QuotaType type) {
		HibernateCallback<List<Long>> action = new HibernateCallback<List<Long>>() {
			public List<Long> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT child.id AS child_id FROM quota AS father");
				sb.append(" JOIN quota AS child");
				sb.append(" ON child.domain_parent_id = father.domain_id");
				sb.append(" AND child.quota_type = :domainType ");
				sb.append(" AND father.domain_parent_id = :domainId ");
				sb.append(" AND father.default_quota_override = false");
				sb.append(" WHERE father.quota_type = :domainType");
				sb.append(" AND child.default_quota_override = false");
				sb.append(";");
				final SQLQuery query = session.createSQLQuery(sb.toString());
				query.setLong("domainId", domain.getPersistenceId());
				query.addScalar("child_id", LongType.INSTANCE);
				query.setString("domainType", type.name());
				@SuppressWarnings("unchecked")
				List<Long> res = query.list();
				logger.debug("child_ids :"  + res);
				return res;
			}
		};
		return getHibernateTemplate().execute(action);
	}

	public Long cascadeDefaultQuotaToSubDomainsDefaultQuota(AbstractDomain domain, Long quota, List<Long> quotaIdList) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("UPDATE Quota SET default_quota = :quota WHERE id IN :list_quota_id ;");
				final Query query = session.createSQLQuery(sb.toString());
				query.setLong("quota", quota);
				query.setParameterList("list_quota_id", quotaIdList);
				return (long) query.executeUpdate();
			}
		};
		long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} defaultQuota of DomainQuota have been updated.", updatedCounter );
		return updatedCounter;
	}

	public List<Long> getQuotaIdforQuotaInSubDomains(AbstractDomain domain, Long quota, QuotaType type) {
		HibernateCallback<List<Long>> action = new HibernateCallback<List<Long>>() {
			public List<Long> doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT child.id AS child_id FROM quota AS father");
				sb.append(" JOIN quota AS child");
				sb.append(" ON child.domain_parent_id = father.domain_id");
				sb.append(" AND child.quota_type = :domainType ");
				sb.append(" AND father.domain_parent_id = :domainId ");
				sb.append(" AND father.quota_override = false");
				sb.append(" WHERE father.quota_type = :domainType");
				sb.append(" AND child.quota_override = false");
				sb.append(";");
				final SQLQuery query = session.createSQLQuery(sb.toString());
				query.setLong("domainId", domain.getPersistenceId());
				query.addScalar("child_id", LongType.INSTANCE);
				query.setString("domainType", type.name());
				@SuppressWarnings("unchecked")
				List<Long> res = query.list();
				logger.debug("child_ids :"  + res);
				return res;
			}
		};
		return getHibernateTemplate().execute(action);
	}

	public Long cascadeDefaultQuotaToSubDomainsQuota(AbstractDomain domain, Long quota, List<Long> quotaIdList) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				StringBuilder sb = new StringBuilder();
				sb.append("UPDATE Quota SET quota = :quota WHERE id IN :list_quota_id ;");
				final Query query = session.createSQLQuery(sb.toString());
				query.setLong("quota", quota);
				query.setParameterList("list_quota_id", quotaIdList);
				return (long) query.executeUpdate();
			}
		};
		long updatedCounter = getHibernateTemplate().execute(action);
		logger.debug(" {} quota of DomainQuota have been updated.", updatedCounter );
		return updatedCounter;
	}
}
