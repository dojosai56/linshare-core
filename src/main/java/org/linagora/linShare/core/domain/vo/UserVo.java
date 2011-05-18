/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.domain.vo;

import java.io.Serializable;
import java.util.Date;

import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.UserType;

/**
 * @author ncharles
 *
 */
public class UserVo implements Serializable {

	private static final long serialVersionUID = 3087781771112041575L;

	private final String login;
	private final String firstName;
	private final String lastName;
	private final String mail;
	private final UserType userType;
	private final Role role;
	private final boolean upload;
	private final boolean createGuest; 
    private String ownerLogin = null;
    private Date expirationDate = null;
    private String comment;
    private String locale;
    private boolean restricted;
    private String domainIdentifier;

    public UserVo(User user) {
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.mail = user.getMail();
        this.userType = user.getUserType();
        this.role = user.getRole();
        this.upload= user.getCanUpload();
        this.createGuest=user.getCanCreateGuest();
        this.locale = user.getLocale();
        if (user instanceof Guest) {
            Guest guest = (Guest) user;
            ownerLogin = guest.getOwner().getLogin();
            expirationDate = (Date)guest.getExpiryDate().clone();
            this.comment = guest.getComment();
            this.restricted = guest.isRestricted();
        }
        if (user.getDomain() != null) {
        	this.domainIdentifier = user.getDomain().getIdentifier();
        }
    }
	
    public UserVo(Guest user) {
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.mail = user.getMail();
        this.userType = user.getUserType();
        this.role = user.getRole();
        this.upload= user.getCanUpload();
        this.createGuest=user.getCanCreateGuest();
        this.ownerLogin = user.getOwner().getLogin();
        this.expirationDate = (Date)user.getExpiryDate().clone();
        this.comment = user.getComment();
        this.locale = user.getLocale();
        this.restricted = user.isRestricted();
        if (user.getDomain() != null) {
        	this.domainIdentifier = user.getDomain().getIdentifier();
        }
    }
	public UserVo(String login, String firstName, String lastName,
			String mail, UserType userType) {
		super();
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.userType = userType;
		this.role = Role.SIMPLE;
		this.upload=true;
		this.createGuest=true;
		this.ownerLogin = "";
        this.restricted = false;
        this.domainIdentifier = null;
	}
	
	public UserVo(String login, String firstName, String lastName,
			String mail, Role role,UserType userType) {
		super();
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.userType = userType;
		this.role = role;
		this.upload=true;
		this.createGuest=true;
        this.restricted = false;
        this.domainIdentifier = null;
	}
	
	public UserVo(String login, String firstName, String lastName,
			String mail, Role role,UserType userType, String locale) {
		super();
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.mail = mail;
		this.userType = userType;
		this.role = role;
		this.upload=true;
		this.createGuest=true;
		this.locale = locale;
        this.restricted = false;
        this.domainIdentifier = null;
	}

	public String getLogin() {
		return login;
	}

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public String getCompleteName() {
        return firstName + " " + lastName;
    }
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getMail() {
		return mail;
	}
	public UserType getUserType() {
		return userType;
	}
	public Role getRole() {
		return role;
	}

    public boolean isAdministrator() {
        return Role.ADMIN.equals(role)||isSuperAdmin();
    }

	public boolean isSuperAdmin() {
		return Role.SUPERADMIN.equals(role);
	}

    public boolean isGuest() {
        return UserType.GUEST.equals(userType);
    }

	public boolean isUpload() {
		return upload;
	}

    public boolean isCreateGuest() {
		return createGuest;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public boolean isRestricted() {
		return restricted;
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserVo other = (UserVo) obj;
        if ((this.login == null) ? (other.login != null) : !this.login.equals(other.login)) {
            return false;
        }
        if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName)) {
            return false;
        }
        if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName)) {
            return false;
        }
        if ((this.mail == null) ? (other.mail != null) : !this.mail.equals(other.mail)) {
            return false;
        }
        if (this.userType != other.userType) {
            return false;
        }
        if (this.role != other.role) {
            return false;
        }
        if (this.upload != other.upload) {
            return false;
        }
        if (this.createGuest != other.createGuest) {
            return false;
        }
        if (this.domainIdentifier != other.domainIdentifier) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.login != null ? this.login.hashCode() : 0);
        hash = 47 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 47 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        hash = 47 * hash + (this.mail != null ? this.mail.hashCode() : 0);
        hash = 47 * hash + (this.userType != null ? this.userType.hashCode() : 0);
        hash = 47 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 47 * hash + (this.domainIdentifier != null ? this.domainIdentifier.hashCode() : 0);
        return hash;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nlogin : ").append(login);
        stringBuffer.append("\nfirst name : ").append(firstName);
        stringBuffer.append("\nlast name : ").append(lastName);
        stringBuffer.append("\nmail : ").append(mail);
        stringBuffer.append("\nuser type : ").append(userType);
        stringBuffer.append("\nrole : ").append(role);
        stringBuffer.append("\ndomain : ").append(domainIdentifier);
        return stringBuffer.toString();
    }
    
    public String getDomainIdentifier() {
		return domainIdentifier;
	}
    
    public void setDomainIdentifier(String domainIdentifier) {
		this.domainIdentifier = domainIdentifier;
	}

}
