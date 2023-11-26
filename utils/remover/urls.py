from django.urls import path

from .views import vocal_remove

urlpatterns = [
    path('vocal_remove/', vocal_remove),
]




