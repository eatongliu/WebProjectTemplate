package com.gpdata.template.base.servlet;

import com.gpdata.template.utils.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


public class FileLoadServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLoadServlet.class);
    private static final String GIF = "image/gif;charset=GB2312";// 设定输出的类型

    private static final String JPG = "image/jpeg;charset=GB2312";

    private static final long serialVersionUID = 1L;

    public FileLoadServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String filePath = ConfigUtil.getConfig("filepath");
        String spec = request.getParameter("f");// 输出图片的类型的标志

        String imagePath = filePath + spec;

        response.reset();

        OutputStream output = response.getOutputStream();// 得到输出流
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;

        File f = new File(imagePath);
        if (!f.exists()) {
            // 判断文件存在
            response.sendError(404);
            return;
        }
        FileInputStream instream = null;
        try {
            instream = new FileInputStream(f);
            String mimeType = this.getServletContext().getMimeType(imagePath);
            response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
            /* response.setContentType(JPG); */

            // ServletContext context = getServletContext();// 得到背景对象
            // 输入缓冲流
            bos = new BufferedOutputStream(output);// 输出缓冲流

            bis = new BufferedInputStream(instream);// 输入缓冲流

            byte data[] = new byte[4096];// 缓冲字节数
            int size = 0;
            size = bis.read(data);
            while (size != -1) {
                bos.write(data, 0, size);
                size = bis.read(data);
            }

            bos.flush();// 清空输出缓冲流

            output.close();
        } catch (FileNotFoundException e) {
            LOGGER.error("Exception {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e2) {
                    LOGGER.error("Exception", e2);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e2) {
                    LOGGER.error("Exception", e2);
                }
            }

        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}