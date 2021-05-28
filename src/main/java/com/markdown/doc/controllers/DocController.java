package com.markdown.doc.controllers;


import com.markdown.doc.dtos.DocDTO;
import com.markdown.doc.exceptions.UserNotAllowedException;
import com.markdown.doc.services.DocService;
import com.markdown.doc.services.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    DocService docService;

    @Autowired
    TokenService tokenService;

    //create his own docs
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public DocDTO createDocument(@RequestBody DocDTO docDTO) {

        docService.createDocument(docDTO);
        return docDTO;
    }

    //fetch his own docs
    @GetMapping("/{userId}/all")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<DocDTO> fetchUserDocs(@PathVariable String userId, HttpServletRequest httpServletRequest) {
        String jwtToken = getJwtTokenFromHeader(httpServletRequest);
        String callerUserId = tokenService.getUserId(jwtToken); //-userId -> issuer on jwt token
        return docService.fetchDocsForUserId(userId, callerUserId);
    }

    //fetch a public documnent
    @GetMapping("/{docId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','ANONYMOUS')")
    public DocDTO fetchDocument(@PathVariable String docId, HttpServletRequest httpServletRequest) {
        String jwtToken = getJwtTokenFromHeader(httpServletRequest);
        String userId = tokenService.getUserId(jwtToken); //-userId -> issuer on jwt token
        return docService.fetchDoc(docId, userId);
    }

    //fetch 10 most recent docs that are public
    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('USER','ADMIN','ANONYMOUS')")
    public List<DocDTO> fetchRecentDocs() {
        return docService.fetchTopRecentDocs();
    }

    //modify his own docs
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public DocDTO updateDocument(@RequestBody DocDTO docDTO, HttpServletRequest httpServletRequest) throws UserNotAllowedException {

        String jwtToken = getJwtTokenFromHeader(httpServletRequest);
        //extract user id from token
        String userId = tokenService.getUserId(jwtToken); //-userId -> issuer on jwt token
        docService.updateDoc(docDTO, userId);

        return docDTO;
    }

    private String getJwtTokenFromHeader(HttpServletRequest httpServletRequest) {
        try {
            String tokenHeader = httpServletRequest.getHeader(AUTHORIZATION);
            return StringUtils.removeStart(tokenHeader, "Bearer").trim();
        } catch (NullPointerException e) {
            return StringUtils.EMPTY;
        }
    }

    //delete his own docs
    @DeleteMapping("/delete/{docId}")
    public List<DocDTO> DeleteDocument(@PathVariable String docId) {

        //TODO: create a service that handles creation logic
        return null;
    }

}
