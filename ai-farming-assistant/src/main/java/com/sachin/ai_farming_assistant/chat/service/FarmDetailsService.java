package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.dto.FarmDetails;
import com.sachin.ai_farming_assistant.chat.entity.Farm;
import com.sachin.ai_farming_assistant.chat.exception.FarmNotFoundException;
import com.sachin.ai_farming_assistant.chat.repository.FarmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FarmDetailsService {

    private final FarmRepository farmRepository;

    public FarmDetailsService(
            FarmRepository farmRepository
    ) {
        this.farmRepository = farmRepository;
    }

    @Transactional(readOnly = true)
    public FarmDetails getFarmDetails(
            Long authenticatedUserId,
            Long requestedFarmId
    ) {
        if (authenticatedUserId == null) {
            throw new IllegalArgumentException(
                    "Authenticated user is required"
            );
        }

        Farm farm;

        if (requestedFarmId != null) {
            farm = farmRepository
                    .findByIdAndUserId(
                            requestedFarmId,
                            authenticatedUserId
                    )
                    .orElseThrow(() ->
                            new FarmNotFoundException(
                                    "Farm not found or access denied"
                            )
                    );
        } else {
            farm = farmRepository
                    .findFirstByUserIdOrderByIdAsc(
                            authenticatedUserId
                    )
                    .orElseThrow(() ->
                            new FarmNotFoundException(
                                    "No farm is registered for this user"
                            )
                    );
        }

        return new FarmDetails(
                farm.getId(),
                farm.getFarmName(),
                farm.getCrop(),
                farm.getFarmSizeAcres(),
                farm.getSoilType(),
                farm.getIrrigationMethod(),
                farm.getLocation()
        );
    }
}