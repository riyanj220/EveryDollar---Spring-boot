package com.EveryDollar.demo.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.EveryDollar.demo.entity.BudgetEntity;
import com.EveryDollar.demo.entity.UserEntity;

@Repository
public interface ReportRepository extends JpaRepository<BudgetEntity, Long> {

    // Fetch Monthly Income
    @Query("SELECT SUM(b.amount) FROM BudgetEntity b WHERE b.user = :user AND MONTH(b.createdAt) = :month")
    Double getMonthlyIncome(@Param("user") UserEntity user, @Param("month") int month);

    // Fetch Monthly Essential Expenses
    @Query("SELECT SUM(b.amount) FROM BudgetEntity b WHERE b.user = :user AND MONTH(b.createdAt) = :month AND b.sourceName = 'essential'")
    Double getEssentialExpenses(@Param("user") UserEntity user, @Param("month") int month);

    // Fetch Monthly Optional Expenses
    @Query("SELECT SUM(b.amount) FROM BudgetEntity b WHERE b.user = :user AND MONTH(b.createdAt) = :month AND b.sourceName = 'optional'")
    Double getOptionalExpenses(@Param("user") UserEntity user, @Param("month") int month);

    // Fetch Total Assets
    @Query("SELECT SUM(n.value) FROM NetworthEntity n WHERE n.user = :user AND n.type = 'asset'")
    BigDecimal getTotalAssets(@Param("user") UserEntity user);

    // Fetch Total Debts
    @Query("SELECT SUM(n.value) FROM NetworthEntity n WHERE n.user = :user AND n.type = 'debt'")
    BigDecimal getTotalDebts(@Param("user") UserEntity user);

    // Count Achieved Goals
    @Query("SELECT COUNT(g) FROM GoalsEntity g WHERE g.user = :user AND g.progressValue >= g.targetValue")
    Long countAchievedGoals(@Param("user") UserEntity user);

    // Count Total Goals
    @Query("SELECT COUNT(g) FROM GoalsEntity g WHERE g.user = :user")
    Long countTotalGoals(@Param("user") UserEntity user);
}