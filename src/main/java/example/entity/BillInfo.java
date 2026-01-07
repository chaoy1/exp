package example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@TableName("bill_info")
public class BillInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ledgerInfoId; // 关联 ledger_info.id

    private String type; // INCOME or EXPENDITURE

    private Long category; // 关联 category_info.id

    private BigDecimal amount;

    private String remark;

    private String createdBy;

    private LocalDateTime createdTime;

    @TableField(exist = false)
    private String ledgerName; // 账本名称

    @TableField(exist = false)
    private String ledgerCurrencyType; // 账本币种

    // Constructors
    public BillInfo() {
    }

    public BillInfo(Long ledgerInfoId, String type, Long category, BigDecimal amount,
                    String remark, String createdBy, LocalDateTime createdTime) {
        this.ledgerInfoId = ledgerInfoId;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.remark = remark;
        this.createdBy = createdBy;
        this.createdTime = createdTime;
    }

    public String getLedgerName() {
        return ledgerName;
    }
    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }
    public String getLedgerCurrencyType() {
        return ledgerCurrencyType;
    }
    public void setLedgerCurrencyType(String ledgerCurrencyType) {
        this.ledgerCurrencyType = ledgerCurrencyType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLedgerInfoId() {
        return ledgerInfoId;
    }

    public void setLedgerInfoId(Long ledgerInfoId) {
        this.ledgerInfoId = ledgerInfoId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillInfo billInfo = (BillInfo) o;
        return Objects.equals(id, billInfo.id) &&
                Objects.equals(ledgerInfoId, billInfo.ledgerInfoId) &&
                Objects.equals(type, billInfo.type) &&
                Objects.equals(category, billInfo.category) &&
                Objects.equals(amount, billInfo.amount) &&
                Objects.equals(remark, billInfo.remark) &&
                Objects.equals(createdBy, billInfo.createdBy) &&
                Objects.equals(createdTime, billInfo.createdTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ledgerInfoId, type, category, amount, remark, createdBy, createdTime);
    }

    // toString
    @Override
    public String toString() {
        return "BillInfo{" +
                "id=" + id +
                ", ledgerInfoId=" + ledgerInfoId +
                ", type='" + type + '\'' +
                ", category=" + category +
                ", amount=" + amount +
                ", remark='" + remark + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}