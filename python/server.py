import os
from http.server import BaseHTTPRequestHandler, HTTPServer
from io import BytesIO
from os import path
from os.path import curdir, sep
from subprocess import call

PORT_NUMBER = 4444


class myServer(BaseHTTPRequestHandler):

    # Handler for the GET requests
    def do_GET(self):

        if self.path == "/cleanup/":
            dir_name = "./data/"
            test = os.listdir(dir_name)
            for item in test:
                if item.endswith(".csv"):
                    os.remove(os.path.join(dir_name, item))

            self.send_response(200)
            self.end_headers()
            self.wfile.write(b'Done')

        if self.path.startswith("/train/"):
            symbol = self.path.replace("/train/", "")
            call(["python", "create_model.py", "-s " + symbol])
            self.send_response(200)
            self.end_headers()
            self.wfile.write(b'Done')

        if self.path.startswith("/prognosis/"):
            file_path = curdir + sep + "data/predict-low-high-" + self.path.replace("/prognosis/", "") + ".csv"
            if path.exists(file_path):
                f = open(file_path)
                self.send_response(200)
                self.end_headers()
                self.wfile.write(str.encode(f.read()))
                f.close()
            else:
                self.send_response(200)
                self.end_headers()
                self.wfile.write(str.encode(""))

    def do_POST(self):
        content_length = int(self.headers['Content-Length'])
        body = self.rfile.read(content_length)
        self.send_response(200)
        self.end_headers()
        response = BytesIO()
        response.write(b'empty')
        self.wfile.write(response.getvalue())

        with open("data/data-" + self.path.replace("/", "") + ".csv", "a") as myfile:
            myfile.write(body.decode("utf-8"))

    def end_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        BaseHTTPRequestHandler.end_headers(self)


try:
    # Create a web server and define the handler to manage the
    # incoming request
    server = HTTPServer(('', PORT_NUMBER), myServer)
    print('Started httpserver on port ', PORT_NUMBER)

    # Wait forever for incoming htto requests
    server.serve_forever()

except KeyboardInterrupt:
    print('^C received, shutting down the web server')
    server.socket.close()
