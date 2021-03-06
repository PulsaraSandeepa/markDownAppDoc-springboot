package com.markdown.doc.services.impl;

import com.markdown.doc.daos.DocDAO;
import com.markdown.doc.dtos.DocDTO;
import com.markdown.doc.exceptions.UserNotAllowedException;
import com.markdown.doc.models.DocModel;
import com.markdown.doc.services.DocService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class DocServiceImpl implements DocService {

    @Autowired
    DocDAO docDAO;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public void createDocument(DocDTO docDTO) {

        checkNotNull(docDTO.getContent());
        checkNotNull(docDTO.getTitle());
        checkNotNull(docDTO.getUserId());

        DocModel docModel = modelMapper.map(docDTO, DocModel.class);

        if (isNull(docModel.getAvailable())) {
            docModel.setAvailable(false);
        }
        docDAO.save(docModel);

        modelMapper.map(docModel, docDTO);

    }

    @Override
    public List<DocDTO> fetchDocsForUserId(String userId, String callerUserId) {

        final List<DocModel> allByUserId = docDAO.findAllByuserIdOrderByUpdatedAtDesc(userId);

        if (userId.equals(callerUserId)) {
            return allByUserId.stream()
                    .map(docModel -> modelMapper.map(docModel, DocDTO.class))
                    .collect(Collectors.toList());
        } else {
            return allByUserId.stream()
                    .filter(DocModel::getAvailable)
                    .map(docModel -> modelMapper.map(docModel, DocDTO.class))
                    .collect(Collectors.toList());
        }


    }

    @Override
    public DocDTO fetchDoc(String docId, String userId) {

        final Optional<DocModel> optionalDocModel = docDAO.findById(docId);

        if (optionalDocModel.isPresent()) {
            if (optionalDocModel.get().getUserId().equals(userId)) {
                return modelMapper.map(optionalDocModel, DocDTO.class);
            } else {
                if (optionalDocModel.get().getUserId().equals(userId)) {
                    return modelMapper.map(optionalDocModel, DocDTO.class);
                }
            }
            return null;
        }
        return null;
    }

    @Override
    public List<DocDTO> fetchTopRecentDocs() {

        final Page<DocModel> recentDocs = docDAO.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt")));

        return recentDocs.stream()
                .map(docModel -> modelMapper.map(docModel, DocDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateDoc(DocDTO docDTO, String userId) throws UserNotAllowedException {

        final Optional<DocModel> optionalDocModel = docDAO.findById(docDTO.getId());

        if (optionalDocModel.isPresent()) {

            final DocModel docModel = optionalDocModel.get();

            if (docModel.getUserId().equals(userId)) {

                modelMapper.map(docDTO, optionalDocModel);
                docDAO.save(docModel);

                modelMapper.map(docModel, docDTO);
                return;

            } else {
                throw new UserNotAllowedException("You are not allowed to modify this document");

            }

        }
        throw new NoSuchElementException("No document ith id " + docDTO.getId() + " was found");

    }
}
