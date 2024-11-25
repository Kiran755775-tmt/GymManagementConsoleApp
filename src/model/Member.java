package model;


import java.sql.Date;

public class Member {
    //ATTRIBUTES
    private int memberId;
    private String memberName;
    private String mailId;
    private String membershipType;
    private Date membershipStartDate;
    private Date getMembershipEndDate;

    //GETTERS AND SETTERS
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public Date getMembershipStartDate() {
        return membershipStartDate;
    }

    public void setMembershipStartDate(Date membershipStartDate) {
        this.membershipStartDate = membershipStartDate;
    }

    public Date getGetMembershipEndDate() {
        return getMembershipEndDate;
    }

    public void setGetMembershipEndDate(Date getMembershipEndDate) {
        this.getMembershipEndDate = getMembershipEndDate;
    }


    //CONSTRUCTORS
    public Member(int memberId, String memberName, String mailId, String membershipType, Date membershipStartDate, Date getMembershipEndDate) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.mailId = mailId;
        this.membershipType = membershipType;
        this.membershipStartDate = membershipStartDate;
        this.getMembershipEndDate = getMembershipEndDate;
    }

    public Member(String memberName, String mailId, String membershipType, Date membershipStartDate, Date getMembershipEndDate) {
        this.memberName = memberName;
        this.mailId = mailId;
        this.membershipType = membershipType;
        this.membershipStartDate = membershipStartDate;
        this.getMembershipEndDate = getMembershipEndDate;
    }

    public Member() {
    }

    //OVERRIDING THE TO STRING METHOD
    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", mailId='" + mailId + '\'' +
                ", membershipType='" + membershipType + '\'' +
                ", membershipStartDate=" + membershipStartDate +
                ", getMembershipEndDate=" + getMembershipEndDate +
                '}';
    }
}
