package eu.suggesto.d40.pdfexport.portlet;

import java.io.OutputStream;
import javax.portlet.PortletException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import eu.suggesto.d40.pdfexport.constants.PdfExportPortletKeys;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.model.JournalArticleDisplay;
import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import java.io.IOException;

import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.StringReader;
import com.itextpdf.tool.xml.ElementHandler;
import com.itextpdf.tool.xml.Writable;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.pipeline.WritableElement;


/**
 * @author 
 *locale:
 * 

 *
 */
@Component(
        immediate = true,
        property = {
            "com.liferay.portlet.header-portlet-css=/css/main.css",
            "com.liferay.portlet.display-category=category.d40",
            "com.liferay.portlet.instanceable=false",
            "com.liferay.portlet.scopeable=true",
            "javax.portlet.display-name=PdfExport",
            "javax.portlet.init-param.template-path=/",
            "javax.portlet.init-param.view-template=/view.jsp",
            "javax.portlet.name=" + PdfExportPortletKeys.PDFEXPORT,
            "javax.portlet.resource-bundle=content.Language",
            "javax.portlet.security-role-ref=guest,administrator,power-user,user"
        },
        service = Portlet.class
)
public class PdfExportPortlet extends MVCPortlet {

  

   

    @Override
    public void serveResource(ResourceRequest resourceRequest,
            ResourceResponse resourceResponse)
            throws IOException, PortletException {
        long groupId = ParamUtil.getLong(resourceRequest, "groupId");
        String articleId = ParamUtil.getString(resourceRequest, "articleId");
        String ddmTemplateKey = ParamUtil.getString(resourceRequest, "ddmTemplateKey");
        String viewMode = "";
        String languageId = LanguageUtil.getLanguageId(resourceRequest);
        ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

        try {

            JournalArticleDisplay articleDisplay = JournalArticleLocalServiceUtil.getArticleDisplay(groupId, articleId, ddmTemplateKey, viewMode, languageId, themeDisplay);

            System.out.println(groupId + "," + articleId + "," + ddmTemplateKey + "," + viewMode + "," + languageId + "," + themeDisplay);

            resourceResponse.reset();
            resourceResponse.setContentType("application/pdf");
            resourceResponse.setProperty("Content-disposition", "attachment; filename=\"" + articleDisplay.getTitle().concat(StringPool.PERIOD).concat("pdf") + "\"");
            OutputStream outputStream = resourceResponse.getPortletOutputStream();
            createPdf(articleDisplay.getTitle(), articleDisplay.getContent(), outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    public void createPdf(String title, String articleHtml, OutputStream outputStream) throws IOException {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            document.addTitle(title);
            XMLWorkerHelper xmlWorker=XMLWorkerHelper.getInstance();
            xmlWorker.parseXHtml(writer, document,
                new StringReader(articleHtml));

            document.close();
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
