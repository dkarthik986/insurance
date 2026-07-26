package com.insurance.agent.notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface ReminderSettingsRepository extends JpaRepository<ReminderSettings, UUID> {}

