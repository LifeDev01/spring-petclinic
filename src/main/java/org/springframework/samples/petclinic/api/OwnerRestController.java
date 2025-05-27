package org.springframework.samples.petclinic.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.web.bind.annotation.*;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.api.dto.OwnerDto;
import org.springframework.samples.petclinic.api.mapper.OwnerMapper;


import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/owners")


public class OwnerRestController {
	private final OwnerRepository ownerRepository;
	private final OwnerMapper ownerMapper;

	@Autowired
	public OwnerRestController(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
		this.ownerRepository = ownerRepository;
		this.ownerMapper = ownerMapper;
	}

	@GetMapping("/{id}")
	public ResponseEntity<OwnerDto> getOwner(@PathVariable("id") int id) {
		Optional<Owner> ownerOpt = ownerRepository.findById(id);
		if (ownerOpt.isPresent()) {
			Owner own = ownerOpt.get();
			OwnerDto dto = ownerMapper.toDto(own);
			return ResponseEntity.ok(dto);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public ResponseEntity<OwnerDto> createOwner(@RequestBody OwnerDto ownerDto) {
		Owner owner = ownerMapper.toEntity(ownerDto);
		Owner savedOwner = ownerRepository.save(owner);
		return ResponseEntity.status(HttpStatus.CREATED).body(ownerMapper.toDto(savedOwner));
	}

	@PutMapping("/{id}")
	public ResponseEntity<OwnerDto> updateOwner(@PathVariable("id") int id, @RequestBody OwnerDto ownerDto) {
		Optional<Owner> existingOwnerOpt = ownerRepository.findById(id);
		if (existingOwnerOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Owner existingOwner = existingOwnerOpt.get();
		existingOwner.setFirstName(ownerDto.getFirstName());
		existingOwner.setLastName(ownerDto.getLastName());
		existingOwner.setCity(ownerDto.getCity());
		existingOwner.setTelephone(ownerDto.getTelephone());

		Owner updatedOwner = ownerRepository.save(existingOwner);
		return ResponseEntity.ok(ownerMapper.toDto(updatedOwner));
	}

	@PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
	public ResponseEntity<OwnerDto> patchOwner(@PathVariable("id") int id, @RequestBody Map<String, Object> patch) {
		Optional<Owner> existingOwnerOpt = ownerRepository.findById(id);
		if (existingOwnerOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Owner owner = existingOwnerOpt.get();
		patch.forEach((key, value) -> {
			switch (key) {
				case "firstName":
					owner.setFirstName((String) value);
				case "lastName":
					owner.setLastName((String) value);
				case "city":
					owner.setCity((String) value);
				case "telephone":
					owner.setTelephone((String) value);
				case "address":
					owner.setAddress((String) value);
			}
		});

		Owner updatedOwner = ownerRepository.save(owner);
		return ResponseEntity.ok(ownerMapper.toDto(updatedOwner));
	}
}
