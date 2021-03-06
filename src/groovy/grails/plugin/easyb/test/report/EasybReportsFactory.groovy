package grails.plugin.easyb.test.report

import org.easyb.report.XmlReportWriter
import org.easyb.report.HtmlReportWriter
import org.easyb.report.TxtStoryReportWriter
import org.easyb.report.TxtSpecificationReportWriter

public class EasybReportsFactory {
    public static final String XML = "xml";
    public static final String PLAIN = "plain";
    public static final String HTML = "html";

    protected final String phaseName;
    protected final String typeName;
    protected final File reportsDir;
    protected final List<String> formats;

    public static EasybReportsFactory createFromBuildBinding(Binding buildBinding) {
        // This is not great, the phase and type names probably shouldn't be sourced from the binding.
        return new EasybReportsFactory(
            (String)buildBinding.getProperty("currentTestPhaseName"),
            (String)buildBinding.getProperty("currentTestTypeName"),
            (File)buildBinding.getProperty("testReportsDir"),
            (List<String>)buildBinding.getProperty("reportFormats")
        );
    }

    public EasybReportsFactory(String phaseName, String typeName, File reportsDir, List<String> formats) {
        this.phaseName = phaseName;
        this.typeName = typeName;
        this.reportsDir = reportsDir;
        this.formats = formats;
    }

    protected def createReport() {
        def reportsWriter = []

        reportsWriter << new HtmlReportWriter(location: "$reportsDir/html/easyb-${phaseName}.html")
        reportsWriter << new TxtStoryReportWriter(location: "$reportsDir/plain/easyb-stories-${phaseName}.txt")
        reportsWriter << new TxtSpecificationReportWriter(location: "$reportsDir/plain/easyb-specifications-${phaseName}.txt")
        //TODO grails core dont create reportsDir/xml, so this results in exception, fix somehow
        //reportsWriter << new XmlReportWriter(location: "$reportsDir/xml/easyb-${phaseName}.xml")

        return reportsWriter
    }

    public void produceReports(def results) {
        def reportsWriter = createReport()
        reportsWriter.each { rw ->
            rw.writeReport(results)
        }
    }
}