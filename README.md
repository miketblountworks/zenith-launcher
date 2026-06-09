# Dextera Launcher

## Overview
Dextera is a modern, highly customizable Android home screen launcher built entirely with Kotlin and Jetpack Compose. It is designed to replace the standard Android interface with a more fluid, premium user experience—focusing on advanced media integrations, an intelligent notification center, and a unified device search engine.

> **🚧 Project Status: Active Development** > Dextera is currently in active development. Core features, UI components, and underlying system architectures are subject to rapid iteration, bug fixes, and continuous improvement.

## Core Features

### 🗂️ Smart Notification Center
A custom-built notification tray that completely reimagines how Android notifications are displayed and interacted with on the home screen.
* **Intelligent Card Stacking:** Notifications from the same application are automatically grouped into interactive, 3D-layered card stacks that expand smoothly when tapped.
* **Robust Intent Handling:** Engineered to safely navigate Android 12+ background activity restrictions, utilizing `ActivityOptions` fallbacks to flawlessly launch target applications from custom UI elements.
* **System Filtering:** Automatically filters out persistent, non-actionable background services (like System UI) to maintain a clutter-free aesthetic.
* **Inline Actions:** Full support for quick replies, marking as read, and manual dismissal directly from the launcher UI.

### 🎵 Interactive Media Hub
A persistent, layout-aware media player deeply integrated directly into the workspace.
* **Physics-Based Gestures:** Swipe down on the album artwork to toggle play/pause, complete with satisfying, custom spring-physics bounce animations and visual feedback overlays.
* **Dynamic Controls:** Floating transport controls and responsive marquee text formatting for lengthy track metadata.
* **Smart Volume Routing:** An integrated volume slider that dynamically detects active media sessions, automatically routing volume commands to either the local device (`STREAM_MUSIC`) or active remote casting sessions (e.g., Smart TVs).

### 🔍 Unified Search Engine
A comprehensive search layer accessible directly from the home screen.
* **Real-Time On-Device Querying:** Instantly search through installed applications, contacts, system settings, and local files.
* **Web Integration:** Seamless handoff for external web queries.
* **Category Filtering:** Quick-tap glassmorphic tabs to instantly filter search results by type.
* **Native Navigation:** Custom back-handler integration to ensure fluid swipe-to-go-back system gestures are respected within the custom UI hierarchy.

### 📱 Modern App Drawer
* Fast A-Z vertical index scrubbing.
* Glassmorphic filter tabs (All Apps, Social, Utilities) designed to maintain maximum legibility against complex, dynamic wallpapers.
* Refined typography featuring subtle drop shadows and expanded start-padding for a clean, breathable layout.

## Tech Stack & Architecture
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **System Integrations:** Deep utilization of Android SDK APIs including `NotificationListenerService`, `MediaController`, `AudioManager`, and robust `PendingIntent` routing.
