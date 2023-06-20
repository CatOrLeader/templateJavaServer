package server;



class ExceptionResponseMaker {
    public static void INTERNAL_ERROR(HttpResponse response) {
        response.setStatusCode(500);
        response.setStatus("Internal server error");
        response.getHeaders().put("Content-Type", Config.TEXT_HTML);
        response.setBody("<html><body>" +
                "<h1>500</h1>" +
                "<div>Resources not found</div>" +
                "</body></html>");
    }

    public static void NOT_FOUND_ERROR(HttpResponse response) {
        response.setStatusCode(404);
        response.setStatus("Not Found");
        response.addHeader("Content-Type", Config.TEXT_HTML);
        response.setBody("<html><body>" +
                "<h1>404</h1>" +
                "<div>Resources not found</div>" +
                "</body></html>");
    }
}
