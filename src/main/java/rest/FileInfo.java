package rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FileInfo {
    private String sender;
    private String filename;
    private String content;

    public FileInfo() {}

    public FileInfo(String sender, String filename, String content) {
        this.sender = sender;
        this.filename = filename;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }
}
