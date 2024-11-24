package com.techtest.scrumretroapi.service;

import com.techtest.scrumretroapi.entity.Retrospective;
import com.techtest.scrumretroapi.entity.feedback.Feedback;
import com.techtest.scrumretroapi.entity.feedback.FeedbackItem;
import com.techtest.scrumretroapi.entity.task.Task;
import com.techtest.scrumretroapi.entity.task.TaskItem;
import com.techtest.scrumretroapi.repository.RetrospectiveRepository;
import com.techtest.scrumretroapi.utils.LoggingUtil;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RetrospectiveService {

    @Autowired
    private RetrospectiveRepository retrospectiveRepository;

    private final Log logger = LoggingUtil.getLogger(RetrospectiveService.class);

    public Page<Retrospective> getAllRetrospectives(Pageable pageable) {
        logger.debug("Attempting to retrieve all retrospectives with pagination");
        return retrospectiveRepository.findAllRetrospectives(pageable);
    }

    public List<Retrospective> getRetrospectivesByDate(LocalDate date) {
        logger.info("Attempting to retrieve retrospectives for date: " + date);
        List<Retrospective> retrospectiveList = retrospectiveRepository.findRetrospectivesByDate(date);
        return retrospectiveList.stream().sorted().toList();
    }

    @Transactional
    public void createNewRetrospective(Retrospective retrospective) throws Exception {
        String nameToCheck = retrospective.getName();
        logger.debug(String.format("Attempting to add new retrospective with name='%s'", nameToCheck));

        Retrospective existingRetrospective = retrospectiveRepository.findByName(nameToCheck);
        boolean nameExists = existingRetrospective != null;

        if (nameExists) {
            String errorMessage = String.format("The retrospective name '%s' has already been added!", nameToCheck);
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        } else {
            logger.info("Creating a new retrospective with name: " + nameToCheck);
            retrospectiveRepository.save(retrospective);
            logger.info("New retrospective created successfully");
        }
    }

    @Transactional
    public void createNewFeedbackForRetrospective(String retrospectiveName, FeedbackItem newFeedbackItem)
            throws Exception {
        // Determine if the name exists and fetch the retrospective object with feedback
        // list
        Retrospective retrospective = retrospectiveRepository.findByName(retrospectiveName);

        if (retrospective == null) {
            logger.warn("Cannot add feedback to Retrospective " + retrospectiveName + ", as it doesn't exist!");
            throw new Exception("Retrospective does not exist");
        } else {
            logger.info("Adding new feedback to retrospective: " + retrospectiveName);
            logger.debug("Adding feedback with values: " + newFeedbackItem);
            List<Feedback> feedbackList = retrospective.getFeedback();
            // generate an item id based on current list size and add 1
            int item = feedbackList.size() + 1;
            feedbackList.add(new Feedback(item, newFeedbackItem));
            retrospective.setFeedback(feedbackList);

            // save the new repository
            retrospectiveRepository.save(retrospective);
        }
    }

    @Transactional
    public void updateFeedbackForRetrospective(String retrospectiveName, int itemId, FeedbackItem feedbackItem)
            throws Exception {
        Retrospective retrospective = retrospectiveRepository.findByName(retrospectiveName);

        if (retrospective == null) {
            logger.warn("Cannot add feedback to Retrospective " + retrospectiveName + ", as it doesn't exist!");
            throw new Exception("Retrospective does not exist");
        } else {
            logger.info("Updating feedback for retrospective: " + retrospectiveName + ", item ID: " + itemId);
            logger.debug("Adding feedback with values: " + feedbackItem);

            // copy the list and extract the feedback item
            List<Feedback> feedbackList = retrospective.getFeedback();
            Feedback feedbackCopy = feedbackList.stream()
                    .filter(fi -> fi.getItem() == itemId)
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.warn("Unable to update feedback for feedback item number: " + itemId
                                + ". Does not exist for retrospective " + retrospectiveName);
                        return new Exception("No feedback with item number: " + itemId
                                + ". Please revise item number or create new feedback");
                    });

            feedbackCopy.setItemBody(feedbackItem);

            List<Feedback> updatedFeedbackList = new java.util.ArrayList<>(feedbackList.stream()
                    .filter(fi -> fi.getItem() != itemId)
                    .toList());

            updatedFeedbackList.add(feedbackCopy);

            retrospective.setFeedback(updatedFeedbackList);
            retrospectiveRepository.save(retrospective);
            logger.info("Successfully updated feedback item " + itemId + " with new feedback for Retrospective "
                    + retrospectiveName);
        }
    }

    @Transactional
    public void createNewTaskForRetrospective(String retrospectiveName, Task newTask) throws Exception {
        Retrospective retrospective = retrospectiveRepository.findByName(retrospectiveName);
        if (retrospective == null) {
            logger.warn("Cannot add task to Retrospective " + retrospectiveName + ", as it doesn't exist!");
            throw new Exception("Retrospective does not exist");
        } else {
            logger.info("Adding new task to retrospective: " + retrospectiveName);
            logger.debug("Adding task with values: " + newTask);

            List<Task> taskList = retrospective.getTask();
            int item = taskList.size() + 1;
            newTask.setItem(item);
            taskList.add(newTask);
            retrospective.setTask(taskList);

            retrospectiveRepository.save(retrospective);
            logger.info("New task added successfully to retrospective: " + retrospectiveName);
        }
    }

    @Transactional
    public void updateTaskForRetrospective(String retrospectiveName, int itemId, TaskItem taskItem) throws Exception {
        Retrospective retrospective = retrospectiveRepository.findByName(retrospectiveName);

        if (retrospective == null) {
            logger.warn("Cannot update task in Retrospective " + retrospectiveName + ", as it doesn't exist!");
            throw new Exception("Retrospective does not exist");
        } else {
            logger.info("Updating task for retrospective: " + retrospectiveName + ", item ID: " + itemId);
            logger.debug("Updating task with values: " + taskItem);

            List<Task> taskList = retrospective.getTask();
            Task taskCopy = taskList.stream()
                    .filter(t -> t.getItem() == itemId)
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.warn("Unable to update task for item number: " + itemId
                                + ". Does not exist for retrospective " + retrospectiveName);
                        return new Exception("No task with item number: " + itemId
                                + ". Please revise item number or create new task");
                    });

            taskCopy.setItemBody(taskItem);

            List<Task> updatedTaskList = new java.util.ArrayList<>(taskList.stream()
                    .filter(t -> t.getItem() != itemId)
                    .toList());

            updatedTaskList.add(taskCopy);

            retrospective.setTask(updatedTaskList);
            retrospectiveRepository.save(retrospective);
            logger.info("Successfully updated task item " + itemId + " for Retrospective " + retrospectiveName);
        }
    }

    public List<Task> getTasksByRetrospectiveName(String retrospectiveName) throws Exception {
        Retrospective retrospective = retrospectiveRepository.findByName(retrospectiveName);

        if (retrospective == null) {
            logger.warn("Cannot retrieve tasks for Retrospective " + retrospectiveName + ", as it doesn't exist!");
            throw new Exception("Retrospective does not exist");
        } else {
            logger.info("Fetching tasks for retrospective: " + retrospectiveName);
            return retrospective.getTask();
        }
    }

}
