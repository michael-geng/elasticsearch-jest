package com.zhuanche.es.jest.bean;


import io.searchbox.annotations.JestId;

import java.io.Serializable;
import java.util.Date;

/**
 * @author workOrder
 * 工单主表
 */
public class WorkSheet implements Serializable {

    @JestId
    private Long id;

    /**
     *工单编号
     */
    private String workSheetNo;

    /**
     *工单类型，一级分类
     */
    private Integer sheetTypeOne;

    /**
     *工单类型，二级分类
     */
    private Integer sheetTypeTwo;

    /**
     *工单类型，三级分类
     */
    private Integer sheetTypeThree;

    /**
     *工单类型，四级分类
     */
    private Integer sheetTypeFour;

    /**
     *工单来源
     */
    private Integer sheetSource=0;

    /**
     *联系方式
     */
    private String contact="";

    /**
     *工单分类，1、订单相关，2，订单无关
     */
    private Integer sheetClassify=1;

    /**
     *订单类型(1:普通用户订单, 2:企业用户订单)
     */
    private Integer orderType=0;

    /**
     *订单类型,1:随叫随到;2:预约用车;3:接机;5:送机;6:日租;7:半日租;10:多日接送；15深港线；16港深线
     */
    private Integer serviceTypeId=0;

    /**
     *司ID
     */
    private Integer driverId=0;

    /**
     *司机姓名
     */
    private String driverName="";

    /**
     *司机手机号
     */
    private String driverPhone="";

    /**
     *服务城市
     */
    private Integer cityId;

    /**
     *车牌号
     */
    private String licensePlates="";

    /**
     *订单号
     */
    private String orderNo="";

    /**
     *乘客姓名
     */
    private String riderName = "";

    /**
     *乘客手机号
     */
    private String riderPhone="";

    /**
     *责任部门
     */
    private Integer dutyDept=0;

    /**
     *1、普通,,2、紧急 3、很紧急
     */
    private Integer sheetPriority = 1;

    /**
     *工单标签
     */
    private Integer sheetTag=0;

    /**
     *处理时效，1、1个小时，3、3个小时，24：1个工作日，48、2个工作日
     */
    private Integer handleTime=1;

    /**
     *备注
     */
    private String memo="";

    /**
     *工单当前状态0、待分配，1、待处理，2、处理完成，3、暂时关闭
     */
    private Integer currentStatus = 0;

    /**
     *提交人ID
     */
    private String commitUserId;

    /**
     *提交人姓名
     */
    private String commitUserName;

    /**
     *当前处理人ID
     */
    private String currentDealUserId="";

    /**
     *当前处理人姓名
     */
    private String currentDealUserName="";

    /**
     *七陌进电ID
     */
    private Long callRecordId = 0L;

    /**
     *工单类型，一级分类
     */
    private Integer confirmSheetTypeOne=0;

    /**
     *工单类型，二级分类
     */
    private Integer confirmSheetTypeTwo=0;

    /**
     *工单类型，三级分类
     */
    private Integer confirmSheetTypeThree=0;

    /**
     *工单类型，四级分类
     */
    private Integer confirmSheetTypeFour=0;

    /**
     *创建时间
     */
    private Date createDate;

    /**
     *修改时间
     */
    private Date updateDate;

    /**
     *部门 id
     */
    private Integer deptId=0;

    /**
     *催促次数
     */
    private Integer urgeTimes = 0;

    /**
     *重新打开次数
     */
    private Integer reopenTimes = 0;

    /**
     *标签的排序值
     */
    private Integer sheetTagSort = 0;

    /**
     *排序权重值
     */
    private Integer weight = 0;

    /**
     * 关注人ids，用于创建工单时传参
     */
    private String attentionUserIds;


    public String getWorkSheetNo() {
        return workSheetNo;
    }

    public void setWorkSheetNo(String workSheetNo) {
        this.workSheetNo = workSheetNo == null ? null : workSheetNo.trim();
    }

    public Integer getSheetTypeOne() {
        return sheetTypeOne;
    }

    public void setSheetTypeOne(Integer sheetTypeOne) {
        this.sheetTypeOne = sheetTypeOne;
    }

    public Integer getSheetTypeTwo() {
        return sheetTypeTwo;
    }

    public void setSheetTypeTwo(Integer sheetTypeTwo) {
        this.sheetTypeTwo = sheetTypeTwo;
    }

    public Integer getSheetTypeThree() {
        return sheetTypeThree;
    }

    public void setSheetTypeThree(Integer sheetTypeThree) {
        this.sheetTypeThree = sheetTypeThree;
    }

    public Integer getSheetTypeFour() {
        return sheetTypeFour;
    }

    public void setSheetTypeFour(Integer sheetTypeFour) {
        this.sheetTypeFour = sheetTypeFour;
    }

    public Integer getSheetSource() {
        return sheetSource;
    }

    public void setSheetSource(Integer sheetSource) {
        this.sheetSource = sheetSource;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact == null ? null : contact.trim();
    }

    public Integer getSheetClassify() {
        return sheetClassify;
    }

    public void setSheetClassify(Integer sheetClassify) {
        this.sheetClassify = sheetClassify;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName == null ? null : driverName.trim();
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone == null ? null : driverPhone.trim();
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates == null ? null : licensePlates.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName == null ? null : riderName.trim();
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone == null ? null : riderPhone.trim();
    }

    public Integer getDutyDept() {
        return dutyDept;
    }

    public void setDutyDept(Integer dutyDept) {
        this.dutyDept = dutyDept;
    }

    public Integer getSheetPriority() {
        return sheetPriority;
    }

    public void setSheetPriority(Integer sheetPriority) {
        this.sheetPriority = sheetPriority;
    }

    public Integer getSheetTag() {
        return sheetTag;
    }

    public void setSheetTag(Integer sheetTag) {
        this.sheetTag = sheetTag;
    }

    public Integer getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Integer handleTime) {
        this.handleTime = handleTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCommitUserId() {
        return commitUserId;
    }

    public void setCommitUserId(String commitUserId) {
        this.commitUserId = commitUserId == null ? null : commitUserId.trim();
    }

    public String getCommitUserName() {
        return commitUserName;
    }

    public void setCommitUserName(String commitUserName) {
        this.commitUserName = commitUserName == null ? null : commitUserName.trim();
    }

    public String getCurrentDealUserId() {
        return currentDealUserId;
    }

    public void setCurrentDealUserId(String currentDealUserId) {
        this.currentDealUserId = currentDealUserId == null ? null : currentDealUserId.trim();
    }

    public String getCurrentDealUserName() {
        return currentDealUserName;
    }

    public void setCurrentDealUserName(String currentDealUserName) {
        this.currentDealUserName = currentDealUserName == null ? null : currentDealUserName.trim();
    }

    public Long getCallRecordId() {
        return callRecordId;
    }

    public void setCallRecordId(Long callRecordId) {
        this.callRecordId = callRecordId;
    }

    public Integer getConfirmSheetTypeOne() {
        return confirmSheetTypeOne;
    }

    public void setConfirmSheetTypeOne(Integer confirmSheetTypeOne) {
        this.confirmSheetTypeOne = confirmSheetTypeOne;
    }

    public Integer getConfirmSheetTypeTwo() {
        return confirmSheetTypeTwo;
    }

    public void setConfirmSheetTypeTwo(Integer confirmSheetTypeTwo) {
        this.confirmSheetTypeTwo = confirmSheetTypeTwo;
    }

    public Integer getConfirmSheetTypeThree() {
        return confirmSheetTypeThree;
    }

    public void setConfirmSheetTypeThree(Integer confirmSheetTypeThree) {
        this.confirmSheetTypeThree = confirmSheetTypeThree;
    }

    public Integer getConfirmSheetTypeFour() {
        return confirmSheetTypeFour;
    }

    public void setConfirmSheetTypeFour(Integer confirmSheetTypeFour) {
        this.confirmSheetTypeFour = confirmSheetTypeFour;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Integer getUrgeTimes() {
        return urgeTimes;
    }

    public void setUrgeTimes(Integer urgeTimes) {
        this.urgeTimes = urgeTimes;
    }

    public Integer getReopenTimes() {
        return reopenTimes;
    }

    public void setReopenTimes(Integer reopenTimes) {
        this.reopenTimes = reopenTimes;
    }

    public Integer getSheetTagSort() {
        return sheetTagSort;
    }

    public void setSheetTagSort(Integer sheetTagSort) {
        this.sheetTagSort = sheetTagSort;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getAttentionUserIds() {
        return attentionUserIds;
    }

    public void setAttentionUserIds(String attentionUserIds) {
        this.attentionUserIds = attentionUserIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}