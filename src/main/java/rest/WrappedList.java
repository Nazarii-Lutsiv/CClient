package rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class WrappedList{

    public List<String> items;

    public WrappedList(){}

    public WrappedList(List<String> items){
        this.items = items;
    }

    public WrappedList(String[] items){
        this.items = new ArrayList<>(Arrays.asList(items));
    }
}
