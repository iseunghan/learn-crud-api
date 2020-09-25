package me.iseunghan.learncrud.common;

public class DuplicatedException extends RuntimeException{
        final String information;

        public DuplicatedException(String information) {
            super(information);
            this.information = information;
        }

        public String getInformation() {
            return information;
        }
}
