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

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class UploadRequestCloseByOwnerEmailBuilder extends GenericUploadRequestEmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.UPLOAD_REQUEST_CLOSED_BY_OWNER;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		res.add(getFakeSingleForRecipient(language));
		res.add(getFakeGroupedForRecipient(language));
		return res;
	}

	private Context getFakeGroupedForRecipient(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));
		recipients.add(new MailContact("unknown2@linshare.org"));

		List<Document> documents = Lists.newArrayList();
		Document document = new Document("a-upload-request-file.txt");
		document.setSize(65985L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#recipientlink");
		documents.add(document);
		document = new Document("a-second-upload-request-file.txt");
		document.setSize(659L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#recipientlink");
		document.setMine(true);
		documents.add(document);

		Context ctx = newFakeContext(language, false, true);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("isgrouped", true);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());
		ctx.setVariable("totalSize", new Long(5682642));

		return ctx;
	}

	private Context getFakeSingleForRecipient(Language language) {
		List<MailContact> recipients = Lists.newArrayList();
		recipients.add(new MailContact("unknown@linshare.org"));
		recipients.add(new MailContact("unknown2@linshare.org"));

		List<Document> documents = Lists.newArrayList();
		Document document = new Document("a-upload-request-file.txt");
		document.setSize(65985L);
		document.setCreationDate(new Date());
		documents.add(document);
		document.setHref(fakeLinshareURL + "/#recipientlink");
		document = new Document("a-second-upload-request-file.txt");
		document.setSize(659L);
		document.setCreationDate(new Date());
		document.setHref(fakeLinshareURL + "/#recipientlink");
		document.setMine(true);
		documents.add(document);

		Context ctx = newFakeContext(language, false, true);
		ctx.setVariable("body", "upload request body message");
		ctx.setVariable("documents", documents);
		ctx.setVariable("documentsCount", documents.size());
		ctx.setVariable("isgrouped", false);
		ctx.setVariable("recipients", recipients);
		ctx.setVariable("recipientsCount", recipients.size());
		ctx.setVariable("totalSize", new Long(5682642));

		return ctx;
	}

}
