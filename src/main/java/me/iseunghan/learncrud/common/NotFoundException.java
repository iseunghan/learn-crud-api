package me.iseunghan.learncrud.common;

public class NotFoundException extends RuntimeException{
    final String information;

    public NotFoundException(String information) {
        super(information);
        this.information = information;
    }

    public String getInformation() {
        return information;
    }

}
