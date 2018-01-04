package com.zk.chat.entity;


/**
 * session 用户信息
 * @author syf
 */
public class SessionInfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1377358687709772113L;
	
	private Integer userId;//用户ID
	private String mobilePhone;//手机号
	private Integer parentId;//主账户ID
	private Integer companyId;//公司ID
	private String lastLoginDate;//最近一次登录时间
	private String contactNameCn;//联系人名称
	private String logoImageUrl;//头像
	private Integer auditStatus;//用户认证状态： 0 -- 未认证  2 - 审核中  3 - 已审核  4 -- 审核未通过
	private Integer tempPlatformFlag;//区分当前redis的用户信息来源 1 PC 2 WX
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public String getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public String getContactNameCn() {
		return contactNameCn;
	}
	public void setContactNameCn(String contactNameCn) {
		this.contactNameCn = contactNameCn;
	}
	public String getLogoImageUrl() {
		return logoImageUrl;
	}
	public void setLogoImageUrl(String logoImageUrl) {
		this.logoImageUrl = logoImageUrl;
	}
	public Integer getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
	public Integer getTempPlatformFlag() {
		return tempPlatformFlag;
	}
	public void setTempPlatformFlag(Integer tempPlatformFlag) {
		this.tempPlatformFlag = tempPlatformFlag;
	}
	
	
}
