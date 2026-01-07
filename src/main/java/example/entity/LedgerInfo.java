package example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@TableName("ledger_info")
public class LedgerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name ;

    private String description;

    private Integer number; // 记账笔数

    private BigDecimal income; // 收入总和

    private BigDecimal expenditure; // 支出总和

    private String currencyType; // 关联 currency_type_info.name

    private String createdBy;

    private LocalDateTime createdTime;

    // Constructors
    public LedgerInfo() {
    }

    public LedgerInfo(String name, String description, Integer number, BigDecimal income,
                      BigDecimal expenditure, String currencyType, String createdBy, LocalDateTime createdTime) {
        this.name = name;
        this.description = description;
        this.number = number;
        this.income = income;
        this.expenditure = expenditure;
        this.currencyType = currencyType;
        this.createdBy = createdBy;
        this.createdTime = createdTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(BigDecimal expenditure) {
        this.expenditure = expenditure;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
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
        LedgerInfo ledgerInfo = (LedgerInfo) o;
        return Objects.equals(id, ledgerInfo.id) &&
                Objects.equals(name, ledgerInfo.name) &&
                Objects.equals(description, ledgerInfo.description) &&
                Objects.equals(number, ledgerInfo.number) &&
                Objects.equals(income, ledgerInfo.income) &&
                Objects.equals(expenditure, ledgerInfo.expenditure) &&
                Objects.equals(currencyType, ledgerInfo.currencyType) &&
                Objects.equals(createdBy, ledgerInfo.createdBy) &&
                Objects.equals(createdTime, ledgerInfo.createdTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, number, income, expenditure, currencyType, createdBy, createdTime);
    }

    // toString
    @Override
    public String toString() {
        return "LedgerInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", number=" + number +
                ", income=" + income +
                ", expenditure=" + expenditure +
                ", currencyType='" + currencyType + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }

}