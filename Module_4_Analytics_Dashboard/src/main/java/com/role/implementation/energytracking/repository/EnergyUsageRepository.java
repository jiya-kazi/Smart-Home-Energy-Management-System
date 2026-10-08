package com.role.implementation.energytracking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.role.implementation.devicemanagement.model.Device;
import com.role.implementation.energytracking.model.EnergyUsage;

@Repository
public interface EnergyUsageRepository extends JpaRepository<EnergyUsage, Long> {

    // =========================================================
    // BASIC QUERIES
    // =========================================================

    List<EnergyUsage> findByDevice(Device device);

    // 🔹 REQUIRED FOR SAFE DEVICE DELETE
    void deleteByDevice(Device device);

    List<EnergyUsage> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // =========================================================
    // USER LEVEL ANALYTICS
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(e.energyConsumed), 0)
        FROM EnergyUsage e
        WHERE e.device.user.id = :userId
    """)
    Double getTotalEnergyByUserId(@Param("userId") Integer userId);

    @Query("""
        SELECT COALESCE(SUM(e.energyConsumed), 0)
        FROM EnergyUsage e
        WHERE e.device.user.id = :userId
          AND e.timestamp BETWEEN :start AND :end
    """)
    Double getTotalEnergyForUserBetween(@Param("userId") Integer userId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    // =========================================================
    // DEVICE LEVEL ANALYTICS
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(e.energyConsumed), 0)
        FROM EnergyUsage e
        WHERE e.device = :device
          AND e.timestamp BETWEEN :start AND :end
    """)
    Double getTotalEnergyForDeviceBetween(@Param("device") Device device,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    // =========================================================
    // ADMIN / SYSTEM LEVEL ANALYTICS
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(e.energyConsumed), 0)
        FROM EnergyUsage e
        WHERE e.timestamp BETWEEN :start AND :end
    """)
    Double getTotalEnergyBetween(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);

    // =========================================================
    // 🔝 TOP ENERGY CONSUMING DEVICES (ADMIN DASHBOARD)
    // =========================================================

    @Query("""
        SELECT e.device.name, COALESCE(SUM(e.energyConsumed), 0)
        FROM EnergyUsage e
        WHERE e.timestamp BETWEEN :start AND :end
        GROUP BY e.device.name
        ORDER BY SUM(e.energyConsumed) DESC
    """)
    List<Object[]> findTopEnergyConsumingDevices(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end,
                                                 Pageable pageable);

    // =========================================================
    // ✅ ADMIN POLICY SUPPORT (SAFE ADDITION)
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(e.energyConsumed), 0)
        FROM EnergyUsage e
        WHERE e.device.id = :deviceId
          AND DATE(e.timestamp) = CURRENT_DATE
    """)
    Optional<Double> findTodayEnergyByDeviceId(@Param("deviceId") Long deviceId);
}
