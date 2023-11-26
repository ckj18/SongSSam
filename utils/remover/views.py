from django.http import HttpResponse
import subprocess

def vocal_remove(request):
    subprocess.call(["python", "spliter.py"])
    return HttpResponse("Vocal removal process is completed.")

