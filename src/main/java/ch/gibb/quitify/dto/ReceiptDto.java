package ch.gibb.quitify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class ReceiptDto {

    private Date date;
    private String name;
    private float sum;
    private String currency;
    private String result;
}
