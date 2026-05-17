package com.noteaiBackend.dto;

import com.noteaiBackend.entity.NoteAtt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoteDTO {
    private String title;
    private String content;
    private String tag;
    private String summary;
    private Byte vision;
    private List<NoteAtt> attachments;
}
