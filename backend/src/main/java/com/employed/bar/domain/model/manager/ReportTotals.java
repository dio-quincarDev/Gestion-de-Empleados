package com.employed.bar.domain.model.manager;


import java.math.BigDecimal;

public class ReportTotals {
    BigDecimal totalRegularHoursWorked;
    BigDecimal totalOvertimeHoursWorked;
    BigDecimal totalEarnings;
    BigDecimal totalConsumptions;
    BigDecimal totalNetPay;


    public BigDecimal getTotalRegularHoursWorked() {
        return totalRegularHoursWorked;
    }

    public void setTotalRegularHoursWorked(BigDecimal totalRegularHoursWorked) {
        this.totalRegularHoursWorked = totalRegularHoursWorked;
    }

    public BigDecimal getTotalOvertimeHoursWorked() {
        return totalOvertimeHoursWorked;
    }

    public void setTotalOvertimeHoursWorked(BigDecimal totalOvertimeHoursWorked) {
        this.totalOvertimeHoursWorked = totalOvertimeHoursWorked;
    }

    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public BigDecimal getTotalConsumptions() {
        return totalConsumptions;
    }

    public void setTotalConsumptions(BigDecimal totalConsumptions) {
        this.totalConsumptions = totalConsumptions;
    }

    public BigDecimal getTotalNetPay() {
        return totalNetPay;
    }

    public void setTotalNetPay(BigDecimal totalNetPay) {
        this.totalNetPay = totalNetPay;
    }

    public ReportTotals(BigDecimal totalRegularHoursWorked, BigDecimal totalOvertimeHoursWorked,
                        BigDecimal totalEarnings, BigDecimal totalConsumptions, BigDecimal totalNetPay) {
        this.totalRegularHoursWorked = totalRegularHoursWorked;
        this.totalOvertimeHoursWorked = totalOvertimeHoursWorked;
        this.totalEarnings = totalEarnings;
        this.totalConsumptions = totalConsumptions;
        this.totalNetPay = totalNetPay;
    }
}
