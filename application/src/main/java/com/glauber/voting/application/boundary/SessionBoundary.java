package com.glauber.voting.application.boundary;

import com.glauber.voting.domain.model.Session;

public interface SessionBoundary {
    Session save(Session session);
    Session findById(Long id);
}
