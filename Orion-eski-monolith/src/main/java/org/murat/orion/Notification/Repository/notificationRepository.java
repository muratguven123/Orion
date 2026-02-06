package org.murat.orion.Notification.Repository;

import org.murat.orion.Notification.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface notificationRepository extends JpaRepository<Notification, Long> {

}
