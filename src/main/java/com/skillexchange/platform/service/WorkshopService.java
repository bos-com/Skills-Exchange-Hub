package com.skillexchange.platform.service;

import com.skillexchange.platform.dto.WorkshopDTO;
import com.skillexchange.platform.entity.Workshop;
import com.skillexchange.platform.entity.University;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.repository.WorkshopRepository;
import com.skillexchange.platform.repository.UniversityRepository;
import com.skillexchange.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkshopService {

    private final WorkshopRepository workshopRepository;
    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;

    public WorkshopService(WorkshopRepository workshopRepository, UniversityRepository universityRepository, 
                          UserRepository userRepository) {
        this.workshopRepository = workshopRepository;
        this.universityRepository = universityRepository;
        this.userRepository = userRepository;
    }

    public Workshop createWorkshop(String title, String description, String hostUniversityId, 
                                  LocalDateTime dateTime, int capacity) {
        Optional<University> universityOpt = universityRepository.findById(hostUniversityId);
        
        if (universityOpt.isPresent()) {
            Workshop workshop = new Workshop(title, universityOpt.get(), dateTime, capacity);
            workshop.setDescription(description);
            return workshopRepository.save(workshop);
        }
        
        return null;
    }

    public Workshop registerStudent(String workshopId, String studentId) {
        Optional<Workshop> workshopOpt = workshopRepository.findById(workshopId);
        Optional<User> studentOpt = userRepository.findById(studentId);
        
        if (workshopOpt.isPresent() && studentOpt.isPresent()) {
            Workshop workshop = workshopOpt.get();
            
            // Check if workshop is full
            if (workshop.isFull()) {
                return null; // Workshop is full
            }
            
            // Add student to registered students
            Set<User> registeredStudents = workshop.getRegisteredStudents();
            if (registeredStudents == null) {
                registeredStudents = new java.util.HashSet<>();
            }
            
            // Check if student is already registered
            if (registeredStudents.contains(studentOpt.get())) {
                return workshop; // Already registered
            }
            
            registeredStudents.add(studentOpt.get());
            workshop.setRegisteredStudents(registeredStudents);
            workshop.incrementRegisteredCount();
            
            return workshopRepository.save(workshop);
        }
        
        return null;
    }

    public Workshop unregisterStudent(String workshopId, String studentId) {
        Optional<Workshop> workshopOpt = workshopRepository.findById(workshopId);
        Optional<User> studentOpt = userRepository.findById(studentId);
        
        if (workshopOpt.isPresent() && studentOpt.isPresent()) {
            Workshop workshop = workshopOpt.get();
            
            // Remove student from registered students
            Set<User> registeredStudents = workshop.getRegisteredStudents();
            if (registeredStudents != null && registeredStudents.contains(studentOpt.get())) {
                registeredStudents.remove(studentOpt.get());
                workshop.setRegisteredStudents(registeredStudents);
                workshop.decrementRegisteredCount();
                
                return workshopRepository.save(workshop);
            }
        }
        
        return null;
    }

    public List<Workshop> getWorkshopsByUniversity(String universityId) {
        Optional<University> universityOpt = universityRepository.findById(universityId);
        return universityOpt.map(workshopRepository::findByHostUniversity)
                .orElse(List.of());
    }

    public List<Workshop> getUpcomingWorkshops() {
        return workshopRepository.findByDateTimeAfter(LocalDateTime.now());
    }

    public List<Workshop> getUpcomingWorkshopsByUniversity(String universityId) {
        Optional<University> universityOpt = universityRepository.findById(universityId);
        return universityOpt.map(university -> 
                workshopRepository.findByHostUniversityAndDateTimeAfter(university, LocalDateTime.now()))
                .orElse(List.of());
    }

    public List<Workshop> getWorkshopsByStudent(String studentId) {
        Optional<User> studentOpt = userRepository.findById(studentId);
        return studentOpt.map(workshopRepository::findByRegisteredStudentsContaining)
                .orElse(List.of());
    }

    public Workshop updateWorkshop(String workshopId, String title, String description, 
                                  LocalDateTime dateTime, int capacity) {
        Optional<Workshop> workshopOpt = workshopRepository.findById(workshopId);
        
        if (workshopOpt.isPresent()) {
            Workshop workshop = workshopOpt.get();
            if (title != null) workshop.setTitle(title);
            if (description != null) workshop.setDescription(description);
            if (dateTime != null) workshop.setDateTime(dateTime);
            if (capacity > 0) workshop.setCapacity(capacity);
            
            return workshopRepository.save(workshop);
        }
        
        return null;
    }

    public boolean deleteWorkshop(String workshopId) {
        if (workshopRepository.existsById(workshopId)) {
            workshopRepository.deleteById(workshopId);
            return true;
        }
        return false;
    }

    public WorkshopDTO convertToDTO(Workshop workshop) {
        WorkshopDTO dto = new WorkshopDTO();
        dto.setId(workshop.getId());
        dto.setTitle(workshop.getTitle());
        dto.setDescription(workshop.getDescription());
        dto.setHostUniversityId(workshop.getHostUniversity().getId());
        dto.setHostUniversityName(workshop.getHostUniversity().getName());
        dto.setDateTime(workshop.getDateTime());
        dto.setCapacity(workshop.getCapacity());
        dto.setRegisteredCount(workshop.getRegisteredCount());
        
        if (workshop.getRegisteredStudents() != null) {
            Set<String> studentIds = workshop.getRegisteredStudents().stream()
                .map(User::getId)
                .collect(Collectors.toSet());
            dto.setRegisteredStudentIds(studentIds);
        }
        
        dto.setCreatedAt(workshop.getCreatedAt());
        dto.setUpdatedAt(workshop.getUpdatedAt());
        return dto;
    }

    public List<WorkshopDTO> convertToDTOList(List<Workshop> workshops) {
        return workshops.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}