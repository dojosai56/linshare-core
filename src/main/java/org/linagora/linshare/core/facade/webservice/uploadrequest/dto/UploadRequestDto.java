package org.linagora.linshare.core.facade.webservice.uploadrequest.dto;

import java.util.Date;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;

import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiModelProperty;

public class UploadRequestDto {

	private String uuid;

	@ApiModelProperty(value = "Owner")
	private ContactDto owner;

	@ApiModelProperty(value = "Recipient")
	private ContactDto recipient;

	// could be null
	private Integer maxFileCount;

	// could be null
	private Long maxDepositSize;

	// could be null
	private Long maxFileSize;

	private Date activationDate;

	// could be null
	private Date expiryDate;

	private boolean canDeleteDocument;

	private boolean canClose;

	private String subject;

	// could be null
	private String body;

	private boolean isClosed;

	private Set<EntryDto> entries = Sets.newHashSet();

	private boolean protectedByPassword;

	private long usedSpace = 0;

	Set<String> extensions = Sets.newHashSet();

	public UploadRequestDto() {
		super();
	}

	public UploadRequestDto(UploadRequest entity) {
		super();
		this.owner = new ContactDto(entity.getOwner());
		this.recipient = null;
		this.maxFileCount = entity.getMaxFileCount();
		this.maxDepositSize = entity.getMaxDepositSize();
		this.maxFileSize = entity.getMaxFileSize();
		this.activationDate = entity.getActivationDate();
		this.expiryDate = entity.getExpiryDate();
		this.canDeleteDocument = entity.isCanDelete();
		this.canClose = entity.isCanClose();
		this.subject = entity.getUploadRequestGroup().getSubject();
		this.body = entity.getUploadRequestGroup().getBody();
		this.isClosed = false;
		if (entity.getStatus().equals(UploadRequestStatus.STATUS_CLOSED))
			this.isClosed = true;
		for (UploadRequestEntry entry : entity.getUploadRequestEntries()) {
			entries.add(new EntryDto(entry));
			this.usedSpace += entry.getSize();
		}
		this.protectedByPassword = false;
	}

	public UploadRequestDto(UploadRequestUrl requestUrl) {
		this(requestUrl.getUploadRequest());
		this.uuid = requestUrl.getUuid();
		this.recipient = new ContactDto(requestUrl.getContact());
		this.protectedByPassword = requestUrl.isProtectedByPassword();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ContactDto getOwner() {
		return owner;
	}

	public void setOwner(ContactDto owner) {
		this.owner = owner;
	}

	public ContactDto getRecipient() {
		return recipient;
	}

	public void setRecipient(ContactDto recipient) {
		this.recipient = recipient;
	}

	public Integer getMaxFileCount() {
		return maxFileCount;
	}

	public void setMaxFileCount(Integer maxFileCount) {
		this.maxFileCount = maxFileCount;
	}

	public Long getMaxDepositSize() {
		return maxDepositSize;
	}

	public void setMaxDepositSize(Long maxDepositSize) {
		this.maxDepositSize = maxDepositSize;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isCanClose() {
		return canClose;
	}

	public void setCanClose(boolean canClose) {
		this.canClose = canClose;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public Set<EntryDto> getEntries() {
		return entries;
	}

	public void setEntries(Set<EntryDto> entries) {
		this.entries = entries;
	}

	public boolean isCanDeleteDocument() {
		return canDeleteDocument;
	}

	public void setCanDeleteDocument(boolean canDeleteDocument) {
		this.canDeleteDocument = canDeleteDocument;
	}

	public boolean isProtectedByPassword() {
		return protectedByPassword;
	}

	public void setProtectedByPassword(boolean protectedByPassword) {
		this.protectedByPassword = protectedByPassword;
	}

	public long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}

	/**
	 * Helpers
	 */
	public void addExtensions(String mimeType) {
		this.extensions.add(mimeType);
	}
}
