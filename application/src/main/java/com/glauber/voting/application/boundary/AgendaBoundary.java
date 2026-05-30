package com.glauber.voting.application.boundary;

import com.glauber.voting.domain.model.Agenda;

public interface AgendaBoundary {
    Agenda save(Agenda agenda);
}
