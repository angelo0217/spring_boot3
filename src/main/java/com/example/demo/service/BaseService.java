package com.example.demo.service;

import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseService<DTO, Entity, ID> {
    protected final JpaRepository<Entity, ID> repository;

    protected BaseService(JpaRepository<Entity, ID> repository) {
        this.repository = repository;
    }

    public DTO create(DTO dto) {
        Entity entity = convertToEntity(dto);
        Entity savedEntity = repository.save(entity);
        return convertToDto(savedEntity);
    }

    public DTO getById(ID id) {
        Entity entity = repository.findById(id).orElse(null);
        if (entity != null) {
            return convertToDto(entity);
        }
        return null;
    }

    public List<DTO> getAll() {
        List<Entity> entities = repository.findAll();
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DTO update(DTO dto) {
        ID id = getDtoId(dto);
        Entity entity = repository.findById(id).orElse(null);
        if (entity != null) {
            BeanUtils.copyProperties(dto, entity);
            Entity updatedEntity = repository.save(entity);
            return convertToDto(updatedEntity);
        }
        return null;
    }

    public boolean delete(ID id) {
        Entity entity = repository.findById(id).orElse(null);
        if (entity != null) {
            repository.delete(entity);
            return true;
        }
        return false;
    }

    protected abstract Entity convertToEntity(DTO dto);

    protected abstract DTO convertToDto(Entity entity);

    protected abstract ID getDtoId(DTO dto);
}
