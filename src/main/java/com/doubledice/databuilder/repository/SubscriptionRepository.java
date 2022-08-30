package com.doubledice.databuilder.repository;

import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ponomarev 21.07.2022
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
