module GTric {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
	requires org.json;
	requires xchart;
	requires java.desktop;
	requires commons.math3;
	requires java.instrument;
	
	opens com.gtric.app.GTric to javafx.fxml;
	opens com.gtric.utils to javafx.base;
	opens com.gtric.app.GTric.controllers to javafx.fxml;
	exports com.gtric.app.GTric;
	exports com.gtric.app.GTric.controllers;
}