/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
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
package org.linagora.linshare.core.facade.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.transformers.impl.ThreadEntryTransformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadMemberVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.ThreadEntryDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class ThreadEntryFacadeImpl extends GenericTapestryFacade implements ThreadEntryFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(ThreadEntryFacadeImpl.class);

	private final ThreadService threadService;

	private final ThreadEntryService threadEntryService;

	@SuppressWarnings("unused")
	private final ThreadEntryTransformer threadEntryTransformer;

	private final DocumentEntryService documentEntryService;

	private final UserService userService;

	public ThreadEntryFacadeImpl(AccountService accountService,
			ThreadService threadService, ThreadEntryService threadEntryService,
			ThreadEntryTransformer threadEntryTransformer,
			DocumentEntryService documentEntryService,
			UserService userService) {
		super(accountService);
		this.threadService = threadService;
		this.threadEntryService = threadEntryService;
		this.threadEntryTransformer = threadEntryTransformer;
		this.documentEntryService = documentEntryService;
		this.userService = userService;
	}

	@Override
	public void copyDocinThread(UserVo actorVo, ThreadVo threadVo,
			DocumentVo documentVo) throws BusinessException {
		User actor = getActor(actorVo);
		// Check if we have the right to access to the specified thread
		Thread thread = threadService.find(actor, actor, threadVo.getLsUuid());
		// Check if we have the right to access to the specified document entry
		DocumentEntry documentEntry = documentEntryService.find(actor, actor, documentVo.getIdentifier());
		// Check if we have the right to download the specified document entry
		InputStream stream = null;
		try {
			stream = documentEntryService.getDocumentStream(actor, actor, documentVo.getIdentifier());
			threadEntryService.copyFromDocumentEntry(actor, actor, thread, documentEntry, stream);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public ThreadVo getThread(UserVo actorVo, String uuid)
			throws BusinessException {
		User actor = getActor(actorVo);
		return new ThreadVo(threadService.find(actor, actor, uuid));
	}

	@Override
	public List<ThreadVo> getAllMyThread(UserVo actorVo)
			throws BusinessException {
		return toThreadVo(threadService.findAllWhereMember(findUser(actorVo)));
	}

	@Override
	public List<ThreadVo> getAllMyThreadWhereCanUpload(UserVo actorVo)
			throws BusinessException {
		return toThreadVo(threadService
				.findAllWhereCanUpload(findUser(actorVo)));
	}

	@Override
	public List<ThreadVo> getAllMyThreadWhereAdmin(UserVo actorVo)
			throws BusinessException {
		return toThreadVo(threadService.findAllWhereAdmin(findUser(actorVo)));
	}

	@Override
	public boolean isUserAdminOfAnyThread(UserVo actorVo)
			throws BusinessException {
		return threadService.hasAnyWhereAdmin(findUser(actorVo));
	}

	@Override
	public boolean userIsAdmin(UserVo userVo, ThreadVo threadVo)
			throws BusinessException {
		return threadService.isUserAdmin(findUser(userVo), findThread(threadVo));
	}

	@Override
	public List<ThreadEntryVo> getAllThreadEntryVo(UserVo actorVo,
			ThreadVo threadVo) throws BusinessException {
		Account actor = findUser(actorVo);
		return toThreadEntryVo(threadEntryService.findAllThreadEntries(actor,
				actor, findThread(threadVo)));
	}

	@Override
	public InputStream retrieveFileStream(UserVo actorVo, ThreadEntryVo entry)
			throws BusinessException {
		Account actor = findUser(actorVo);
		return threadEntryService.getDocumentStream(actor, actor,
				entry.getIdentifier());
	}

	@Override
	public boolean documentHasThumbnail(UserVo actorVo, String documentEntryId) {
		try {
			return threadEntryService.documentHasThumbnail(
					findUser(actorVo), documentEntryId);
		} catch (BusinessException e) {
			logger.error("Can't get if document has thumbnail : "
					+ documentEntryId + " : " + e.getMessage());
			return false;
		}
	}

	@Override
	public InputStream getDocumentThumbnail(UserVo actorVo,
			String docEntryUuid) {
		try {
			return threadEntryService.getDocumentThumbnailStream(
					findUser(actorVo), docEntryUuid);
		} catch (BusinessException e) {
			logger.error("Can't get document thumbnail : " + docEntryUuid
					+ " : " + e.getMessage());
			return null;
		}
	}

	@Override
	public void removeDocument(UserVo actorVo, ThreadEntryVo entryVo)
			throws BusinessException {
		User actor = findUser(actorVo);
		ThreadEntry threadEntry = findEntry(actor, entryVo.getIdentifier());

		threadEntryService.deleteThreadEntry(actor, actor, threadEntry);
	}

	@Override
	public long countEntries(ThreadVo threadVo) throws BusinessException {
		return threadService.countEntries(findThread(threadVo));
	}

	@Override
	public ThreadEntryVo findById(UserVo actorVo, String entryUuid)
			throws BusinessException {
		return new ThreadEntryVo(findEntry(findUser(actorVo), entryUuid));
	}

	@Override
	public boolean userIsMember(UserVo userVo, ThreadVo threadVo)
			throws BusinessException {
		return findMember(findThread(threadVo), findUser(userVo)) != null;
	}

	@Override
	public boolean userCanUpload(UserVo userVo, ThreadVo threadVo)
			throws BusinessException {
		ThreadMember member = findMember(findThread(threadVo), findUser(userVo));

		return member.getCanUpload() || member.getAdmin();
	}

	@Override
	public List<ThreadMemberVo> getThreadMembers(UserVo actorVo,
			ThreadVo threadVo) throws BusinessException {
		User actor = findUser(actorVo);

		return Ordering.natural().immutableSortedCopy(
				toThreadMemberVo(threadService.findAllThreadMembers(actor,
						actor, findThread(threadVo))));
	}

	@Override
	public void addMember(UserVo actorVo, ThreadVo threadVo, UserVo newMember,
			boolean readOnly) throws BusinessException {
		User actor = findUser(actorVo);

		threadService.addMember(actor, actor,
				findThread(threadVo), findOrCreateUser(newMember), false, !readOnly);
	}

	@Override
	public void deleteMember(UserVo actorVo, ThreadVo threadVo,
			ThreadMemberVo memberVo) throws BusinessException {
		Account actor = findUser(actorVo);
		@SuppressWarnings("unused")
		User user = findUser(memberVo.getUser());
		threadService.deleteMember(actor, actor, threadVo.getLsUuid(),
				memberVo.getLsUuid());
	}

	@Override
	public void updateMember(UserVo actorVo, ThreadMemberVo memberVo,
			ThreadVo threadVo) throws BusinessException {
		Account actor = findUser(actorVo);
		threadService.updateMember(actor, actor, threadVo.getLsUuid(), memberVo.getUser().getLsUuid(),
				memberVo.isAdmin(), memberVo.isCanUpload());
	}

	@Override
	public void createThread(UserVo actorVo, String name)
			throws BusinessException {
		User actor = findUser(actorVo);
		threadService.create(actor, actor, name);
	}

	@Override
	public void deleteThread(UserVo actorVo, ThreadVo threadVo)
			throws BusinessException {
		User actor = findUser(actorVo);
		threadService.deleteThread(actor, actor, findThread(threadVo));
	}

	@Override
	public void updateFileProperties(String lsUid, String threadEntryUuid,
			String fileComment, String newName) throws BusinessException {
		Account actor = accountService.findByLsUuid(lsUid);
		//TODO: Add metadata field on tapestry interface, then get the value in the field.
		threadEntryService.updateFileProperties(actor, threadEntryUuid,
				fileComment, null,  newName);
	}

	@Override
	public ThreadEntryVo getThreadEntry(UserVo actorVo, String threadEntryUuid)
			throws BusinessException {
		return new ThreadEntryVo(findEntry(findUser(actorVo), threadEntryUuid));
	}

	@Override
	public List<ThreadVo> searchThread(UserVo userVo, String pattern)
			throws BusinessException {
		User actor = (User) accountService.findByLsUuid(userVo.getLsUuid());
		Set<Thread> res = Sets.newHashSet();

		res.addAll(threadService.searchByName(actor, pattern));
		res.addAll(threadService.searchByMembers(actor, pattern));
		return toThreadVo(res);
	}

	@Override
	public void renameThread(UserVo actorVo, ThreadVo threadVo, String threadName)
			throws BusinessException {
		User actor = findUser(actorVo);
		threadService.update(actor, actor, threadVo.getLsUuid(), threadName);
	}

	@Override
	public void addMember(UserVo actorVo, ThreadVo threadVo,
			String domain, String mail) throws BusinessException {
		User user = userService.findOrCreateUser(mail, domain);
		User actor = findUser(actorVo);

		threadService.addMember(actor, actor, findThread(threadVo), user,
				false, true);
	}

	@Override
	public void removeMember(UserVo actorVo, ThreadVo threadVo,
			UserVo userVo) throws BusinessException {
		User actor = findUser(actorVo);
		threadService.deleteMember(actor, actor, threadVo.getLsUuid(), userVo.getLsUuid());
	}

	@Override
	public List<ThreadVo> getLatestThreads(UserVo actorVo, int limit)
			throws BusinessException {
		return toThreadVo(threadService.findLatestWhereMember(
				findUser(actorVo), limit));
	}

	@Override
	public long countMembers(UserVo actorVo, ThreadVo threadVo)
			throws BusinessException {
		return threadService.countMembers(findThread(threadVo));
	}

	@Override
	public boolean memberIsDeletable(UserVo actorVo, ThreadVo threadVo)
			throws BusinessException {
		return countMembers(actorVo, threadVo) != 1;
	}


	/*
	 * Helpers.
	 */

	/*
	 * Guava helpers, as Lists.transform return a lazy loaded list of proxies.
	 * Avoid LazyLoadingException from the Hibernate proxies as Session is
	 * bound to the facade layer.
	 */
	/*
	 * TODO: externalize these :
	 *    - transformable VO interfaces (toVo)
	 *    - generic helper that transform transformable VOs
	 */
	private List<ThreadVo> toThreadVo(Collection<Thread> col) {
		return ImmutableList.copyOf(Lists.transform(ImmutableList.copyOf(col),
				ThreadVo.toVo()));
	}

	private List<ThreadMemberVo> toThreadMemberVo(Collection<ThreadMember> col) {
		return ImmutableList.copyOf(Lists.transform(ImmutableList.copyOf(col),
				ThreadMemberVo.toVo()));
	}

	private List<ThreadEntryVo> toThreadEntryVo(Collection<ThreadEntry> col) {
		return ImmutableList.copyOf(Lists.transform(ImmutableList.copyOf(col),
				ThreadEntryVo.toVo()));
	}

	/*
	 * Use theses instead of their respective services to ensure proper error
	 * handling and cleaner code.
	 */

	private User findUser(UserVo userVo) throws BusinessException {
		User u = (User) accountService.findByLsUuid(userVo.getLsUuid());

		if (u == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Cannot find user : " + userVo.getLsUuid());
		}
		return u;
	}

	private User findOrCreateUser(UserVo userVo) throws BusinessException {
		User u = userService.findOrCreateUser(userVo.getMail(),
				userVo.getDomainIdentifier());

		if (u == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"Cannot find user : " + userVo.getLsUuid());
		}
		return u;
	}

	private Thread findThread(ThreadVo threadVo) throws BusinessException {
		Thread t = threadService.findByLsUuidUnprotected(threadVo.getLsUuid());

		if (t == null) {
			throw new BusinessException(BusinessErrorCode.THREAD_NOT_FOUND,
					"Cannot find thread : " + threadVo.getLsUuid());
		}
		return t;
	}

	private ThreadMember findMember(Thread thread, User user)
			throws BusinessException {
		ThreadMember m = threadService.getMemberFromUser(thread, user);

		if (m == null) {
			throw new BusinessException(BusinessErrorCode.THREAD_MEMBER_NOT_FOUND,
					"Cannot find member from user : " + user.getMail()
							+ " in thread : " + thread.getLsUuid());
		}
		return m;
	}

	private ThreadEntry findEntry(User actor, String entryUuid)
			throws BusinessException {
		ThreadEntry e = threadEntryService.findById(actor, actor, entryUuid);

		if (e == null) {
			throw new BusinessException(BusinessErrorCode.THREAD_ENTRY_NOT_FOUND,
					"Cannot find thread entry : " + entryUuid);
		}
		return e;
	}


    /*
     * Thou Shalt Not Trespass For Thy Life Is Endangered
     */

	@Deprecated
	@Override
	public List<UserVo> searchAmongUsers(UserVo userVo, String input)
			throws BusinessException {
		List<User> results = new ArrayList<User>();
		List<UserVo> finalResults = new ArrayList<UserVo>();
		User owner = (User) accountService.findByLsUuid(userVo.getLogin());

		input = StringUtils.defaultString(input);
		if (input.startsWith("\"") && input.endsWith(">")) {
			UserVo tmp = MailCompletionService.getUserFromDisplay(input);
			results = userService.searchUser(tmp.getMail(),
					tmp.getFirstName(), tmp.getLastName(), null, owner);
		} else {
			results = performSearch(owner, input);
		}
		for (User currentUser : results) {
			finalResults.add(new UserVo(currentUser));
		}
		return finalResults;
	}

	@Deprecated
	@Override
	public List<ThreadMemberVo> searchAmongMembers(UserVo actorVo,
			ThreadVo currentThread, String input, String criteriaOnSearch)
			throws BusinessException {
		List<ThreadMemberVo> finalResults = new ArrayList<ThreadMemberVo>();
		User owner = (User) accountService.findByLsUuid(actorVo.getLogin());
		List<ThreadMemberVo> listOfMembers = this.getThreadMembers(actorVo,
				currentThread);
		List<User> listSelected = new ArrayList<User>();

		input = StringUtils.defaultString(input);
		if (input.startsWith("\"") && input.endsWith(">")) {
			UserVo selected = MailCompletionService.getUserFromDisplay(input);
			listSelected = userService.searchUser(selected.getMail(),
					selected.getFirstName(), selected.getLastName(), null,
					owner);
		} else if (input.equals("*")) {
			for (ThreadMemberVo threadMemberVo : listOfMembers) {
				listSelected.add((User) accountService
						.findByLsUuid(threadMemberVo.getUser().getLogin()));
			}
		} else {
			listSelected = performSearch(owner, input);
		}
		for (User currentUser : listSelected) {
			for (ThreadMemberVo threadMemberVo : listOfMembers) {
				if (threadMemberVo.getUser().getMail()
						.equals(currentUser.getMail())) {
					if (criteriaOnSearch.equals("admin")
							&& threadMemberVo.isAdmin()) {
						finalResults.add(threadMemberVo);
					} else if (criteriaOnSearch.equals("simple")
							&& threadMemberVo.isCanUpload()
							&& !(threadMemberVo.isAdmin())) {
						finalResults.add(threadMemberVo);
					} else if (criteriaOnSearch.equals("restricted")
							&& !(threadMemberVo.isCanUpload())) {
						finalResults.add(threadMemberVo);
					} else if (criteriaOnSearch.equals("all")) {
						finalResults.add(threadMemberVo);
					}
				}
			}
		}
		Collections.sort(finalResults);
		return finalResults;
	}

	@Deprecated
	private List<User> performSearch(User actor, String pattern)
			throws BusinessException {
		String firstName_ = null;
		String lastName_ = null;
		Set<User> userSet = new HashSet<User>();
		ArrayList<User> finalList = new ArrayList<User>();

		if (pattern != null && pattern.length() > 0) {
			StringTokenizer stringTokenizer = new StringTokenizer(pattern, " ");
			if (stringTokenizer.hasMoreTokens()) {
				firstName_ = stringTokenizer.nextToken();
				if (stringTokenizer.hasMoreTokens()) {
					lastName_ = stringTokenizer.nextToken();
				}
			}
		}

		try {
			if (pattern != null) {
				userSet.addAll(userService.searchUser(pattern.trim(), null,
						null, null, actor));
				userSet.addAll(userService.searchUser(null, pattern.trim(),
						null, null, actor));
				userSet.addAll(userService.searchUser(null, null,
						pattern.trim(), null, actor));
			} else {
				userSet.addAll(userService.searchUser(null, firstName_,
						lastName_, null, actor));
			}
		} catch (BusinessException e) {
			logger.error("Error while searching user", e);
		}
		for (User user : userSet) {
			User current = userService.findOrCreateUser(user.getMail(),
					user.getDomainId());
			if (!(finalList.contains(current))) {
				finalList.add(current);
			}
		}
		return finalList;
	}

	@Deprecated
	@Override
	public List<String> completionOnThreads(UserVo actor, String input) {
		List<ThreadVo> lists = new ArrayList<ThreadVo>();

		if (actor.isSuperAdmin()) {
			// NOTREACHED
		} else {
			try {
				lists = this.getAllMyThread(actor);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<String> elements = new ArrayList<String>();

		for (ThreadVo current : lists) {
			if (current.getName().toLowerCase().contains(input.toLowerCase())) {
				elements.add(current.getName());
			}
		}
		return elements;
	}

	@Deprecated
	@Override
	public List<String> completionOnUsers(UserVo actorVo, String pattern)
			throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLogin());
		List<String> ret = new ArrayList<String>();
		List<User> userSet = performSearch(actor, pattern);

		for (User user : userSet) {
			String completeName = MailCompletionService
					.formatLabel(new UserVo(user))
					.substring(
							0,
							MailCompletionService.formatLabel(new UserVo(user))
									.length() - 1).trim();
			if (!ret.contains(completeName)) {
				ret.add(completeName);
			}
		}
		return ret;
	}

	@Deprecated
	@Override
	public List<String> completionOnMembers(UserVo actorVo,
			ThreadVo currentThread, String pattern) {
		List<String> ret = new ArrayList<String>();

		try {
			for (ThreadMemberVo threadMember : this.getThreadMembers(actorVo,
					currentThread)) {
				if (threadMember.getMail().toLowerCase()
						.contains(pattern.toLowerCase())
						|| threadMember.getFullName().toLowerCase()
								.contains(pattern.toLowerCase())) {
					UserVo user = threadMember.getUser();
					String completeName = MailCompletionService.formatLabel(
							user)
							.substring(
									0,
									MailCompletionService.formatLabel(user)
											.length() - 1);
					if (!ret.contains(completeName)) {
						ret.add(completeName);
					}
				}

			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
		}
		return ret;
	}
}