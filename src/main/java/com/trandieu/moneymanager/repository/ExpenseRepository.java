package com.trandieu.moneymanager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trandieu.moneymanager.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

      // select * from expenses where profile_id = ? order by date desc
      List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

      // select * from expenses where profile_id = ? order by date desc limit 5
      List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

      // select sum(amount) from expenses where profile_id = ?
      @Query("SELECT SUM (e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
      BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

      // select * from expenses where profile_id = ? and date between ? and ? and name
      // like %?% order by date desc
      List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                  Long profileId,
                  LocalDate startDate,
                  LocalDate endDate,
                  String keyword,
                  Sort sort);

      // select * from expenses where profile_id = ? and date between ? and ?
      List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

      // select * from expenses where profile_id = ? and date = ?
      List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);
}
