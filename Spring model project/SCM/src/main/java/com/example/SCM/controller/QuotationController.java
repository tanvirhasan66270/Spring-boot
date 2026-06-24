package com.example.SCM.controller;

import com.example.SCM.dto.request.QuotationRequestDTO;
import com.example.SCM.dto.request.SupplierRequestDTO;
import com.example.SCM.dto.response.QuotationResponseDTO;
import com.example.SCM.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/quotations/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // আপনার ফ্রন্টএন্ড পোর্ট অনুযায়ী এটি পরিবর্তন করতে পারেন
public class QuotationController {

    private final QuotationService quotationService;

    /**
     * ১. নতুন কোটেশন তৈরি এবং ইমেজ আপলোড (Create Operation)
     * যেহেতু এখানে ডাটা এবং ফাইল একসাথে আসবে, তাই consume টাইপ MULTIPART_FORM_DATA_VALUE হওয়া বাধ্যতামূলক।
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuotationResponseDTO> createQuotation(
            @RequestPart("quotation") String quotationJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
       QuotationRequestDTO dto = mapper.readValue(quotationJson, QuotationRequestDTO.class);

        return new ResponseEntity<>(
                quotationService.save(dto, image),
                HttpStatus.CREATED
        );
    }

    /**
     * ২. আইডি দিয়ে নির্দিষ্ট কোটেশন খুঁজে বের করা (Read Operation - Single)
     */
    @GetMapping("{id}")
    public ResponseEntity<QuotationResponseDTO> getQuotationById(@PathVariable Long id) {
        return quotationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ৩. সব কোটেশনের লিস্ট দেখা (Read Operation - List)
     */
    @GetMapping
    public ResponseEntity<List<QuotationResponseDTO>> getAllQuotations() {
        List<QuotationResponseDTO> list = quotationService.findAll();
        return ResponseEntity.ok(list);
    }

    /**
     * ৪. এক্সিস্টিং কোটেশন আপডেট করা (Update Operation)
     * (নোট: আপনার সার্ভিস অনুযায়ী এই মেথডটি শুধু DTO আপডেট করে)
     */
    @PutMapping("{id}")
    public ResponseEntity<QuotationResponseDTO> updateQuotation(
            @PathVariable Long id,
            @RequestBody QuotationRequestDTO dto) {

        QuotationResponseDTO response = quotationService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * ৫. কোটেশন ডিলিট করা (Delete Operation)
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteQuotation(@PathVariable Long id) {
        quotationService.delete(id);
        return ResponseEntity.ok("Quotation deleted successfully with id: " + id);
    }
}