package com.aenggukland.letspt.dietfeedback;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DietFeedbackMapper {
    void save(DietFeedback dietFeedback);

    void update(@Param("feedbackId") Long feedbackId, @Param("type") DietFeedbackType type);

    void delete(Long feedbackId);

    Optional<DietFeedback> findById(Long feedbackId);

    Optional<DietFeedback> findByBoardAndTrainer(@Param("boardId") Long boardId, @Param("trainerId") Long trainerId);

    List<DietFeedbackResponse> findByBoardId(Long boardId);
}
