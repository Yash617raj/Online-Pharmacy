package com.cap.catalog_service.service;

import com.cap.catalog_service.dto.*;
import com.cap.catalog_service.entity.*;
import com.cap.catalog_service.exception.ApiException;
import com.cap.catalog_service.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock private MedicineRepository medicineRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private PrescriptionRepository prescriptionRepository;
    @Mock private ModelMapper mapper;

    @InjectMocks private CatalogService service;

    private Medicine createMedicine() {
        Category category = new Category();
        category.setName("Painkiller");

        Medicine med = new Medicine();
        med.setId(1L);
        med.setName("Paracetamol");
        med.setCategory(category);
        return med;
    }

    @Test
    void getAllMedicines_Success() {
        when(medicineRepository.findAll())
                .thenReturn(List.of(createMedicine()));

        List<MedicineResponse> result = service.getAllMedicines();

        assertEquals(1, result.size());
    }

    @Test
    void getAllMedicines_Empty() {
        when(medicineRepository.findAll()).thenReturn(List.of());

        List<MedicineResponse> result = service.getAllMedicines();

        assertTrue(result.isEmpty());
    }

    @Test
    void getMedicine_Success() {
        when(medicineRepository.findById(1L))
                .thenReturn(Optional.of(createMedicine()));

        MedicineResponse res = service.getMedicine(1L);

        assertNotNull(res);
    }

    @Test
    void getMedicine_NotFound() {
        when(medicineRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.getMedicine(1L));
    }

    @Test
    void searchMedicines_Success() {
        when(medicineRepository.findByNameContainingIgnoreCase("para"))
                .thenReturn(List.of(createMedicine()));

        List<MedicineResponse> result = service.searchMedicines("para");

        assertEquals(1, result.size());
    }

    @Test
    void searchMedicines_NoResult() {
        when(medicineRepository.findByNameContainingIgnoreCase("xyz"))
                .thenReturn(List.of());

        assertTrue(service.searchMedicines("xyz").isEmpty());
    }

    @Test
    void createCategory_Success() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Painkiller");

        when(categoryRepository.findByName("Painkiller"))
                .thenReturn(Optional.empty());

        service.createCategory(dto);

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_AlreadyExists() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Painkiller");

        when(categoryRepository.findByName("Painkiller"))
                .thenReturn(Optional.of(new Category()));

        assertThrows(ApiException.class,
                () -> service.createCategory(dto));
    }

    @Test
    void createMedicine_Success() {
        MedicineRequest req = new MedicineRequest();
        req.setCategoryId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(new Category()));
        when(mapper.map(req, Medicine.class))
                .thenReturn(new Medicine());

        service.createMedicine(req);

        verify(medicineRepository).save(any(Medicine.class));
    }

    @Test
    void createMedicine_CategoryNotFound() {
        MedicineRequest req = new MedicineRequest();
        req.setCategoryId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.createMedicine(req));
    }

    @Test
    void getAllPrescriptions_Success() {
        when(prescriptionRepository.findAll())
                .thenReturn(List.of(new Prescription()));

        assertEquals(1, service.getAllPrescriptions().size());
    }

    @Test
    void uploadPrescription_Success() {
        PrescriptionUploadRequest req = new PrescriptionUploadRequest();
        req.setMedicineId(1L);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.png");

        req.setFile(file);

        when(medicineRepository.findById(1L))
                .thenReturn(Optional.of(new Medicine()));

        service.uploadPrescription(req, "user@gmail.com");

        verify(prescriptionRepository).save(any(Prescription.class));
    }

    @Test
    void uploadPrescription_FileEmpty() {
        PrescriptionUploadRequest req = new PrescriptionUploadRequest();
        req.setMedicineId(1L);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        req.setFile(file);

        when(medicineRepository.findById(1L))
                .thenReturn(Optional.of(new Medicine()));

        assertThrows(ApiException.class,
                () -> service.uploadPrescription(req, "user@gmail.com"));
    }

    @Test
    void uploadPrescription_MedicineNotFound() {
        PrescriptionUploadRequest req = new PrescriptionUploadRequest();
        req.setMedicineId(1L);

        when(medicineRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.uploadPrescription(req, "user@gmail.com"));
    }

    @Test
    void updatePrescriptionStatus_Success() {
        Prescription p = new Prescription();

        when(prescriptionRepository.findById(1L))
                .thenReturn(Optional.of(p));

        service.updatePrescriptionStatus(1L, "APPROVED");

        assertEquals("APPROVED", p.getStatus());
        verify(prescriptionRepository).save(p);
    }

    @Test
    void updatePrescriptionStatus_NotFound() {
        when(prescriptionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.updatePrescriptionStatus(1L, "APPROVED"));
    }
}