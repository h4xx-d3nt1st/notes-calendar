package com.example.notes.controller;

import com.example.notes.entity.Note;
import com.example.notes.repository.NoteRepository;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExportController {

  private final NoteRepository noteRepository;

  public ExportController(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
  }

  @GetMapping("/api/v1/notes/export")
  public void exportXlsx(
      HttpServletResponse response,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to)
      throws Exception {

    if (from == null || to == null || to.isBefore(from)) {
      response.sendError(400, "from/to required and to >= from");
      return;
    }

    List<Note> all = noteRepository.findAllByDateBetweenOrderByDateAscIndexInDayAsc(from, to);

    try (XSSFWorkbook wb = new XSSFWorkbook()) {
      Sheet sh = wb.createSheet("Notes");
      int r = 0;
      Row header = sh.createRow(r++);
      header.createCell(0).setCellValue("ID");
      header.createCell(1).setCellValue("Date");
      header.createCell(2).setCellValue("IndexInDay");
      header.createCell(3).setCellValue("Content");

      for (Note n : all) {
        Row row = sh.createRow(r++);
        row.createCell(0).setCellValue(n.getId());
        row.createCell(1).setCellValue(n.getDate().toString());
        row.createCell(2).setCellValue(n.getIndexInDay());
        row.createCell(3).setCellValue(n.getContent());
      }
      for (int c = 0; c < 4; c++) {
        sh.autoSizeColumn(c);
      }

      String fname = String.format("notes_%s_%s.xlsx", from, to);
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader("Content-Disposition", "attachment; filename=" + fname);
      wb.write(response.getOutputStream());
    }
  }
}
