package com.lingotower.dto.mapper;

import com.lingotower.dto.quiz.QuestionDTO;
import com.lingotower.model.Question;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionMapper {

	public QuestionDTO toDTO(Question question) {
		QuestionDTO dto = new QuestionDTO(question.getId(), question.getQuestionText(),
				combineOptions(question.getCorrectAnswer(), question.getWrongAnswers()), question.getCorrectAnswer(),
				question.getCategory());
		return dto;
	}

	private List<String> combineOptions(String correctAnswer, List<String> wrongAnswers) {
		List<String> options = new ArrayList<>(wrongAnswers);
		options.add(correctAnswer);
		return options;
	}
}