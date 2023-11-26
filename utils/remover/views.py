from django.http import HttpResponse
import subprocess

def vocal_remove(request):
    subprocess.call(["python", "vocal_remove.py"])
    return HttpResponse("Vocal removal process is completed.")

