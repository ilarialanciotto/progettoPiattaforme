package org.ilaria.progetto.Service;

import lombok.RequiredArgsConstructor;
import org.ilaria.progetto.Model.DTO.FeedbackDTO;
import org.ilaria.progetto.Repository.FeedbackRepository;
import org.ilaria.progetto.Service.Mapper.FeedbackMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;

    public void save(FeedbackDTO feedback) {
        feedbackRepository.save(feedbackMapper.toEntity(feedback));
    }

}
