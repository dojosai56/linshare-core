/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.notifications.emails.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.dto.Attribute;
import org.linagora.linshare.core.notifications.context.FakeBuildEmailContext;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.core.notifications.dto.Share;
import org.linagora.linshare.core.notifications.dto.Variable;
import org.linagora.linshare.core.notifications.emails.IEmailBuilder;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class EmailBuilder implements IEmailBuilder {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected TemplateEngine templateEngine;

	protected boolean insertLicenceTerm;

	protected MailActivationBusinessService mailActivationBusinessService;

	protected FunctionalityReadOnlyService functionalityReadOnlyService;

	private DomainBusinessService domainBusinessService;

	private String receivedSharesUrlSuffix;

	private String documentsUrlSuffix;

	public EmailBuilder() {
	}

	public EmailBuilder(TemplateEngine templateEngine, boolean insertLicenceTerm,
			MailActivationBusinessService mailActivationBusinessService,
			FunctionalityReadOnlyService functionalityReadOnlyService, DomainBusinessService domainBusinessService) {
		super();
		this.templateEngine = templateEngine;
		this.insertLicenceTerm = insertLicenceTerm;
		this.mailActivationBusinessService = mailActivationBusinessService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.domainBusinessService = domainBusinessService;
	}

	public void setTemplateEngine(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public void setInsertLicenceTerm(boolean insertLicenceTerm) {
		this.insertLicenceTerm = insertLicenceTerm;
	}

	public void setMailActivationBusinessService(MailActivationBusinessService mailActivationBusinessService) {
		this.mailActivationBusinessService = mailActivationBusinessService;
	}

	public void setFunctionalityReadOnlyService(FunctionalityReadOnlyService functionalityReadOnlyService) {
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	public void setDomainBusinessService(DomainBusinessService domainBusinessService) {
		this.domainBusinessService = domainBusinessService;
	}

	public void setReceivedSharesUrlSuffix(String receivedSharesUrlSuffix) {
		this.receivedSharesUrlSuffix = receivedSharesUrlSuffix;
	}

	public void setDocumentsUrlSuffix(String documentsUrlSuffix) {
		this.documentsUrlSuffix = documentsUrlSuffix;
	}

	public String getReceivedSharesUrlSuffix() {
		return receivedSharesUrlSuffix;
	}

	public String getDocumentsUrlSuffix() {
		return documentsUrlSuffix;
	}

	protected abstract MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException;

	protected abstract List<Context> getContextForFakeBuild(Language language);

	@Override
	public MailContainerWithRecipient build(EmailContext context) throws BusinessException {
		checkSupportedTemplateType(context);
		computeFromDomain(context);
		context.validateRequiredField();
		if (context.getLanguage() == null) {
			context.setLanguage(Language.ENGLISH);
		}
		if (isDisable(context)) {
			return null;
		}
		return buildMailContainer(context);
	}

	@Override
	public MailContainerWithRecipient fakeBuild(MailConfig cfg, Language language, Integer flavor)
			throws BusinessException {
		List<Context> contexts = getContextForFakeBuild(language);
		EmailContext emailContext = new FakeBuildEmailContext(language);
		if (contexts == null || contexts.isEmpty()) {
			throw new BusinessException(BusinessErrorCode.TEMPLATE_PARSING_ERROR,
					"Missing or empty context for fake build.");
		}
		Context ctx = contexts.get(0);
		if (flavor != null) {
			ctx = contexts.get(flavor);
		}
		return buildMailContainerThymeleaf(cfg, getSupportedType(), ctx, emailContext);
	}

	protected void checkSupportedTemplateType(EmailContext context) {
		if (!context.getType().equals(getSupportedType())) {
			logger.error("You can not use this builder {} with the current context {}.", getSupportedType(),
					context.getType());
			throw new BusinessException(BusinessErrorCode.TEMPLATE_PROCESSING_ERROR_INVALID_CONTEXT,
					"You can not use this builder with the current context.");
		}
	}

	protected void computeFromDomain(EmailContext context) {
		AbstractDomain recipientDomain = context.getFromDomain();
		if (context.isNeedToRetrieveGuestDomain()) {
			recipientDomain = domainBusinessService.findGuestDomain(recipientDomain);
			// guest domain could be inexistent into the database.
			if (recipientDomain == null) {
				recipientDomain = context.getFromDomain();
			}
			context.updateFromDomain(recipientDomain);
		}
	}

	protected boolean isDisable(EmailContext context) {
		MailActivation mailActivation = mailActivationBusinessService.findForInternalUsage(context.getFromDomain(),
				context.getActivation());
		return !mailActivation.isEnable();
	}

	protected String getLinShareUrl(Account recipient) {
		String value = functionalityReadOnlyService.getCustomNotificationUrlFunctionality(recipient.getDomain())
				.getValue();
		if (!value.endsWith("/")) {
			return value + "/";
		}
		return value;
	}

	protected String getLinShareAnonymousURL(Account sender) {
		StringValueFunctionality notificationUrl = functionalityReadOnlyService
				.getAnonymousURLNotificationUrl(sender.getDomain());
		return notificationUrl.getValue();
	}

	protected String getFromMailAddress(AbstractDomain domain) {
		if (domain == null) {
			return null;
		}
		String fromMail = functionalityReadOnlyService.getDomainMailFunctionality(domain).getValue();
		return fromMail;
	}

	protected MailContainerWithRecipient buildMailContainerThymeleaf(MailConfig cfg, MailContentType type, Context ctx,
			EmailContext emailCtx) throws BusinessException {
		logger.debug("Building mail content: " + type);
		MailContainerWithRecipient container = new MailContainerWithRecipient(emailCtx.getLanguage());

		// default context
		Map<String, Object> templateResolutionAttributes = Maps.newHashMap();
		templateResolutionAttributes.put("mailConfig", cfg);
		templateResolutionAttributes.put("lang", emailCtx.getLanguage());

		try {
			TemplateSpec subjectSpec = new TemplateSpec(type.toString() + ":subject", null, TemplateMode.TEXT,
					templateResolutionAttributes);
			String subject = templateEngine.process(subjectSpec, ctx);
			ctx.setVariable("mailSubject", subject);

			// TODO manage images integration.
			// "<img src='cid:image.part.1@linshare.org' /><br/><br/>";
			// .add("image", displayLogo ? LINSHARE_LOGO : "")

			TemplateSpec templateSpec = new TemplateSpec(type.toString(), null, null, templateResolutionAttributes);
			// TemplateSpec templateSpec = new TemplateSpec(type.toString(),
			// null, TemplateMode.XML, templateResolutionAttributes);
			String body = templateEngine.process(templateSpec, ctx);
			container.setSubject(subject);
			container.setContent(body);

			container.setFrom(getFromMailAddress(emailCtx.getFromDomain()));
			container.setReplyTo(emailCtx.getMailReplyTo());
			container.setRecipient(emailCtx.getMailRcpt());

			// Message IDs from Web service API (ex Plugin Thunderbird)
			container.setInReplyTo(emailCtx.getInReplyTo());
			container.setReferences(emailCtx.getReferences());
			return container;
		} catch (org.thymeleaf.exceptions.TemplateInputException e) {
			String message = "[" + type.toString() + "]" + getCauseMsessage(e);
			logger.debug(message);
			BusinessException businessException = new BusinessException(getBusinessErrorCodeRecursif(e), message, e);
			throw businessException;
		}
	}

	protected String getCauseMsessage(TemplateInputException e) {
		return getCauseMsessage(e, null);
	}

	protected String getCauseMsessage(Throwable e, String message) {
		if (message == null) {
			message = formatTMLException(e);
		} else {
			message += formatTMLException(e);
		}
		if (e.getCause() != null) {
			message = getCauseMsessage(e.getCause(), message);
		}
		return message;
	}

	protected String formatTMLException(Throwable e) {
		String message = "";
		if (e.getClass().getSimpleName().equals("TemplateInputException")) {
			message = "[TemplateInputException]";
			if (e.getCause() == null) {
				message += e.getMessage();
			}
		} else if (e.getClass().getSimpleName().equals("TextParseException")) {
			message = "[TextParseException]" + e.getMessage();
		} else if (e.getClass().getSimpleName().equals("NoSuchPropertyException")) {
			message = "[NoSuchPropertyException]";
		} else if (e.getClass().getSimpleName().equals("TemplateProcessingException")) {
			message = "[TemplateProcessingException]";
		} else {
			message = "[" + e.getClass().getSimpleName() + "]" + e.getMessage();
		}
		return message;
	}

	protected BusinessErrorCode getBusinessErrorCodeRecursif(Throwable e) {
		BusinessErrorCode errorCode = getBusinessErrorCode(e);
		if (e.getCause() != null) {
			errorCode = getBusinessErrorCodeRecursif(e.getCause());
		}
		return errorCode;
	}

	protected BusinessErrorCode getBusinessErrorCode(Throwable e) {
		if (e.getClass().getSimpleName().equals("TemplateInputException")) {
			// Probably a missing template/fragment exception
			return BusinessErrorCode.TEMPLATE_PARSING_ERROR_TEMPLATE_INPUT_EXCEPTION;
		} else if (e.getClass().getSimpleName().equals("TextParseException")) {
			return BusinessErrorCode.TEMPLATE_PARSING_ERROR_TEXT_PARSE_EXCEPTION;
		} else if (e.getClass().getSimpleName().equals("NoSuchPropertyException")) {
			return BusinessErrorCode.TEMPLATE_PARSING_ERROR_NO_SUCH_PROPERTY_EXCEPTION;
		} else if (e.getClass().getSimpleName().equals("TemplateProcessingException")) {
			return BusinessErrorCode.TEMPLATE_PARSING_ERROR_TEMPLATE_PROCESSING_EXCEPTION;
		}
		return BusinessErrorCode.TEMPLATE_PARSING_ERROR;
	}

	@Override
	public List<ContextMetadata> getAvailableVariables() {
		List<ContextMetadata> res = Lists.newArrayList();
		List<Context> ctx = getContextForFakeBuild(Language.ENGLISH);
		for (Context context : ctx) {
			res.add(getAvailableVariables(context));
		}
		return res;
	}

	private ContextMetadata getAvailableVariables(Context ctx) {
		ContextMetadata metadata = new ContextMetadata(getSupportedType().toString());
		if (ctx == null) {
			return metadata;
		}
		Set<String> variableNames = ctx.getVariableNames();
		for (String name : variableNames) {
			Object obj = ctx.getVariable(name);
			Variable variable = new Variable(name, obj.getClass().getSimpleName());
			logger.debug(variable.toString());
			if (obj instanceof ArrayList) {
				List<?> array = (List<?>) obj;
				if (!array.isEmpty()) {
					Object next = array.iterator().next();
					String parametrizedClassName = next.getClass().getSimpleName();
					variable.setType(variable.getType() + "<" + parametrizedClassName + ">");
					variable.setAttributes(getFields(next));
				}
			} else {
				variable.setAttributes(getFields(obj));
			}
			metadata.addVariable(variable);
		}
		return metadata;
	}

	private List<Attribute> getFields(Object obj) {
		List<Attribute> attributes = null;
		if (isSupportedFieldType(obj)) {
			attributes = Lists.newArrayList();
			for (Field field : obj.getClass().getDeclaredFields()) {
				String typeName = field.getGenericType().getTypeName();
				try {
					Class<?> act = Class.forName(typeName);
					typeName = act.getSimpleName();
				} catch (ClassNotFoundException e) {
					// Do not care.
				}
				Attribute attr = new Attribute(field.getName(), typeName);
				logger.debug(attr.toString());
				attributes.add(attr);
			}
		}
		return attributes;
	}

	private boolean isSupportedFieldType(Object obj) {
		return obj instanceof Document || obj instanceof Share || obj instanceof MailContact;
	}

	protected String getOwnerDocumentLink(String linshareURL, String documentUuid) {
		StringBuilder sb = new StringBuilder();
		sb.append(linshareURL);
		Formatter formatter = new Formatter(sb);
		formatter.format(documentsUrlSuffix, documentUuid);
		formatter.close();
		return sb.toString();
	}

	protected List<Document> transformDocuments(Set<DocumentEntry> documentEntries) {
		return transform(documentEntries, false, null);
	}

	protected List<Document> transform(Set<DocumentEntry> documentEntries, boolean withLink, String linshareURL) {
		List<Document> documents = Lists.newArrayList();
		for (DocumentEntry documentEntry : documentEntries) {
			Document d = new Document(documentEntry);
			if (withLink) {
				d.setHref(getOwnerDocumentLink(linshareURL, documentEntry.getUuid()));
			}
			documents.add(d);

		}
		return documents;
	}

	protected Document getNewFakeDocument(String name) {
		return getNewFakeDocument(name, null);
	}

	protected Document getNewFakeDocument(String name, String linshareURL) {
		Document document = new Document(name);
		if (linshareURL != null) {
			document.setHref(getOwnerDocumentLink(linshareURL, document.getUuid()));
		}
		return document;
	}

	protected Share getNewFakeShare(String name) {
		return getNewFakeShare(name, null);
	}

	protected Share getNewFakeShare(String name, String linshareURL) {
		Share share = new Share(name);
		if (linshareURL != null) {
			share.setHref("Unkown");
			// share.setHref(getOwnerDocumentLink(linshareURL,
			// share.getUuid()));
		}
		return share;
	}

}
