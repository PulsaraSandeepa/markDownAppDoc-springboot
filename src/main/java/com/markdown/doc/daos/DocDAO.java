package com.markdown.doc.daos;

import com.markdown.doc.models.DocModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocDAO extends MongoRepository<DocModel, String> {
    List<DocModel> findAllByuserIdOrderByUpdatedAtDesc(String userId);
}
