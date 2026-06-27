package com.example.SCM.serviceImp;

import com.example.SCM.dto.mapper.QCInspectorMapper;
import com.example.SCM.dto.request.QCInspectorRequestDTO;
import com.example.SCM.dto.response.QCInspectorResponseDTO;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.QCInspector;
import com.example.SCM.entity.User;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.repository.QCInspectorRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.service.QCInspectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QCInspectorServiceImp implements QCInspectorService {

    private final QCInspectorRepository qcInspectorRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final QCInspectorMapper qcInspectorMapper;

    @Value("${image.upload.dir}")
    private String uploadDir;


    @Transactional
    @Override
    public QCInspectorResponseDTO save(QCInspectorRequestDTO dto, MultipartFile image) {
        if (dto == null) {
            throw new IllegalArgumentException("QC Inspector data cannot be null");
        }

        // ম্যাপার স্ট্রাকচার ব্যবহার করে সোর্স অফ ট্রুথ User অবজেক্ট তৈরি
        User user = qcInspectorMapper.toUserEntity(dto);

        // প্লেইন টেক্সট পাসওয়ার্ড এবং ইউজার অ্যাকাউন্ট ডিটিও-র কনফ্লিক্ট-ফ্রি ফিল্ড 'userActive' থেকে সিঙ্কড
        user.setPassword(dto.getPassword());
        user.setActive(dto.isUserActive());

        User savedUser = userRepository.save(user);

        // পুলিশ স্টেশন কুয়েরি করা
        PoliceStation policeStation = null;
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not found with ID: " + dto.getPoliceStationId()));
        }

        // ম্যাপার দিয়ে মূল প্রোফাইল এনটিটি তৈরি করা
        QCInspector inspector = qcInspectorMapper.toQCInspectorEntity(dto, savedUser, policeStation);

        //'qc_inspector' সাব-ফোল্ডারে ইমেজ আপলোড হ্যান্ডেলিং
        if (image != null && !image.isEmpty()) {
            inspector.setImage(uploadImage(image, dto.getName()));
        }

        QCInspector savedInspector = qcInspectorRepository.save(inspector);
        return qcInspectorMapper.toResponseDTO(savedInspector);
    }


    @Transactional
    @Override
    public QCInspectorResponseDTO update(Long id, QCInspectorRequestDTO dto, MultipartFile image) {
        if (dto == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        // N+1 কোয়েরি অপ্টিমাইজড কাস্টম রিপোজিটরি মেথড দিয়ে ডেটা লোড
        QCInspector inspector = qcInspectorRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("QC Inspector not found with ID: " + id));

        // পুলিশ স্টেশন রি-অ্যাসাইনমেন্ট চেক
        PoliceStation policeStation = inspector.getPoliceStation();
        if (dto.getPoliceStationId() != null && (policeStation == null || !dto.getPoliceStationId().equals(policeStation.getId()))) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Selected Police Station not found with ID: " + dto.getPoliceStationId()));
        }

        // ম্যাপার দিয়ে কোর প্রোফাইল ও ইউজার এনটিটি একসাথে ক্যাসকেড আপডেট
        qcInspectorMapper.updateEntity(dto, inspector, policeStation);

        // যদি নতুন পাসওয়ার্ড আসে তবে তা সরাসরি অ্যাসাইন করা
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            inspector.getUser().setPassword(dto.getPassword());
        }

        // নতুন ইমেজ আসলে পুরানো ইমেজ পাথ আপডেট করা
        if (image != null && !image.isEmpty()) {
            inspector.setImage(uploadImage(image, dto.getName()));
        }

        QCInspector updatedInspector = qcInspectorRepository.save(inspector);
        return qcInspectorMapper.toResponseDTO(updatedInspector);
    }


    @Override
    @Transactional(readOnly = true)
    public List<QCInspectorResponseDTO> findAll() {
        return qcInspectorRepository.findAllInspectors().stream()
                .map(qcInspectorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<QCInspectorResponseDTO> getById(Long id) {
        return qcInspectorRepository.findByIdWithDetails(id)
                .map(qcInspectorMapper::toResponseDTO);
    }


    @Transactional
    @Override
    public void delete(Long id) {
        QCInspector inspector = qcInspectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QC Inspector not found with ID: " + id));

        User user = inspector.getUser();

        // রেফারেন্সিয়াল ইন্টেগ্রিটি মেনে প্রথমে প্রোফাইল ও পরে ইউজার ডিলিট করা
        qcInspectorRepository.delete(inspector);
        if (user != null) {
            userRepository.delete(user);
        }
    }


    private String uploadImage(MultipartFile file, String name) {
        try {
            Path path = Paths.get(uploadDir, "qc_inspector");

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();

            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            // নাম থেকে স্পেস রিমুভ করে ইউনিক UUID জেনারেট করা
            String fileName = name.trim().replaceAll("\\s+", "_")
                    + "_" + UUID.randomUUID()
                    + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName));

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed for QC Inspector profile: " + e.getMessage(), e);
        }
    }
}