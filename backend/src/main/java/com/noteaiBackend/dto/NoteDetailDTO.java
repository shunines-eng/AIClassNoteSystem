package com.noteaiBackend.dto;

import com.noteaiBackend.entity.Note;
import com.noteaiBackend.entity.NoteAtt;
import com.noteaiBackend.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class NoteDetailDTO {
    private Note note;
    private List<Tag> tags;
    private List<NoteAtt> attachments;
}
