---
version: "1.0"
name: FreeStyle Libre Alarm
description: A calm, human alarm companion for FreeStyle Libre CGM users. Clear by day, impossible to miss at night.
colors:
  primary: "#c43e2c"
  primary-active: "#a83222"
  primary-disabled: "#e8d8d5"
  accent: "#0077a3"
  accent-active: "#005f82"
  success: "#2d7a3e"
  warning: "#c49600"
  error: "#b52b1f"
  bg: "#ffffff"
  surface: "#f5f4f3"
  surface-elevated: "#ffffff"
  hairline: "#e7e5e3"
  ink: "#1c1917"
  body: "#3d3a38"
  muted: "#6b6866"
  muted-soft: "#9a9795"
  on-primary: "#ffffff"
  dark-bg: "#0f0e0d"
  dark-surface: "#1a1817"
  dark-surface-elevated: "#252321"
  dark-hairline: "#2e2b29"
  dark-ink: "#f2f0ee"
  dark-body: "#d8d6d3"
  dark-muted: "#9a9795"
  dark-muted-soft: "#767370"
  dark-primary: "#e85a4a"
  dark-primary-active: "#f27a6b"
  dark-on-primary: "#ffffff"
typography:
  display:
    fontFamily: "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
    fontSize: "36px"
    fontWeight: 600
    lineHeight: 1.15
    letterSpacing: "-0.02em"
  headline:
    fontFamily: "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
    fontSize: "24px"
    fontWeight: 600
    lineHeight: 1.25
    letterSpacing: "-0.01em"
  title:
    fontFamily: "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
    fontSize: "18px"
    fontWeight: 600
    lineHeight: 1.35
    letterSpacing: "0"
  body:
    fontFamily: "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
    fontSize: "16px"
    fontWeight: 400
    lineHeight: 1.5
    letterSpacing: "0"
  label:
    fontFamily: "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
    fontSize: "14px"
    fontWeight: 500
    lineHeight: 1.25
    letterSpacing: "0.01em"
  caption:
    fontFamily: "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
    fontSize: "12px"
    fontWeight: 500
    lineHeight: 1.35
    letterSpacing: "0.02em"
rounded:
  sm: "4px"
  md: "8px"
  lg: "12px"
  xl: "16px"
  pill: "9999px"
  full: "9999px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "12px"
  lg: "16px"
  xl: "24px"
  xxl: "32px"
  section: "48px"
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.on-primary}"
    typography: "{typography.label}"
    rounded: "{rounded.md}"
    padding: "16px 24px"
    height: "56px"
  button-primary-active:
    backgroundColor: "{colors.primary-active}"
    textColor: "{colors.on-primary}"
  button-primary-disabled:
    backgroundColor: "{colors.primary-disabled}"
    textColor: "{colors.muted}"
  button-secondary:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.ink}"
    typography: "{typography.label}"
    rounded: "{rounded.md}"
    padding: "16px 24px"
    height: "56px"
  button-ghost:
    backgroundColor: "transparent"
    textColor: "{colors.accent}"
    typography: "{typography.label}"
  card:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.ink}"
    rounded: "{rounded.lg}"
    padding: "24px"
  card-elevated:
    backgroundColor: "{colors.surface-elevated}"
    textColor: "{colors.ink}"
    rounded: "{rounded.lg}"
    padding: "24px"
  input:
    backgroundColor: "{colors.bg}"
    textColor: "{colors.ink}"
    typography: "{typography.body}"
    rounded: "{rounded.md}"
    padding: "12px 16px"
    height: "48px"
  badge-low:
    backgroundColor: "{colors.error}"
    textColor: "{colors.on-primary}"
    typography: "{typography.caption}"
    rounded: "{rounded.sm}"
    padding: "4px 10px"
  badge-high:
    backgroundColor: "{colors.warning}"
    textColor: "{colors.ink}"
    typography: "{typography.caption}"
    rounded: "{rounded.sm}"
    padding: "4px 10px"
  switch:
    backgroundColor: "{colors.hairline}"
    textColor: "{colors.primary}"
    rounded: "{rounded.pill}"
  nav-item:
    backgroundColor: "transparent"
    textColor: "{colors.muted}"
    typography: "{typography.label}"
  nav-item-active:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.primary}"
---

# Design System: FreeStyle Libre Alarm

## 1. Overview

**Creative North Star: "The Nightstand Companion"**

This is the visual system for an alarm companion that sits next to a person while they sleep. The interface is calm, human, and quietly confident during the day, then transforms into something impossible to miss at night. Every choice answers one question: can someone half-asleep, under stress, understand what is happening in a single glance?

The system rejects clinical coldness, fitness-app playfulness, and marketing-page theatricality. It borrows clarity from Apple Health — clean type, generous spacing, clear states — but softens the edges so the app feels like it was made for a loved one, not issued by a hospital. Surfaces are nearly pure. Color is used sparingly and with intent. Motion is limited to state changes. Nothing competes with the alarm when it fires.

**Key Characteristics:**
- One humanist sans (Inter) for every role. No display/body pairing; consistency is the voice.
- Restrained color strategy: pure white floor, one warm-red primary for alarms and primary actions, one cool-blue accent for information and links.
- Large, friendly touch targets. The alarm dismiss button is 56px high and full-width.
- Flat, tonal layering. Depth comes from surface color, not shadows.
- Day and night modes are first-class. Dark mode is near-black with warm-tinted neutrals to keep it from feeling cold.

## 2. Colors

The palette is built for safety clarity. Warm red is reserved for real alarm moments and the actions that stop them; cool blue handles secondary information. Neutrals are nearly pure, with just enough warm tint to keep the app from feeling sterile.

### Primary
- **Alarm Red** (`{colors.primary}` — #c43e2c): The alarm color. Used for the full-screen alarm surface, stop-alarm actions, primary CTAs, and active switch tracks. It is warm, urgent, and human — not a cold emergency red.
- **Alarm Red Pressed** (`{colors.primary-active}` — #a83222): Hover/pressed state for primary actions. Darker and slightly desaturated.
- **Alarm Red Disabled** (`{colors.primary-disabled}` — #e8d8d5): Disabled or loading state for primary actions. Light, desaturated, clearly inactive.

### Accent
- **Info Blue** (`{colors.accent}` — #0077a3): Links, secondary buttons, update prompts, and informational highlights. Distinct from the red family in both hue and lightness so it never reads as an alarm.
- **Info Blue Pressed** (`{colors.accent-active}` — #005f82): Pressed state for accent actions.

### Semantic
- **Success Green** (`{colors.success}` — #2d7a3e): Positive status, "monitoring active" indicators.
- **Warning Amber** (`{colors.warning}` — #c49600): High-glucose badge. Used on light surfaces with dark text.
- **Error Red** (`{colors.error}` — #b52b1f): Low-glucose badge, destructive actions, inline errors.

### Neutral (Light Mode)
- **Pure White Floor** (`{colors.bg}` — #ffffff): Default page background. Pure white keeps the app feeling clean and daylight-readable.
- **Soft Surface** (`{colors.surface}` — #f5f4f3): Card backgrounds, section bands, bottom navigation. One step above the floor.
- **Elevated Surface** (`{colors.surface-elevated}` — #ffffff): Cards that need to lift slightly above another surface; matches the floor.
- **Hairline** (`{colors.hairline}` — #e7e5e3): Borders, dividers, input strokes. Warm-tinted gray.
- **Ink** (`{colors.ink}` — #1c1917): Headlines, primary text, icons.
- **Body** (`{colors.body}` — #3d3a38): Running text. Slightly lighter than ink for long-form readability.
- **Muted** (`{colors.muted}` — #6b6866): Secondary labels, placeholders, disabled text.
- **Muted Soft** (`{colors.muted-soft}` — #9a9795): Tertiary text, timestamps, fine print.

### Neutral (Dark Mode)
- **Near-Black Floor** (`{colors.dark-bg}` — #0f0e0d): Default dark background. Warm enough to avoid the cold "developer tool" feeling.
- **Dark Surface** (`{colors.dark-surface}` — #1a1817): Cards and panels.
- **Dark Elevated Surface** (`{colors.dark-surface-elevated}` — #252321): Elevated cards, bottom navigation.
- **Dark Hairline** (`{colors.dark-hairline}` — #2e2b29): Borders and dividers in dark mode.
- **Dark Ink** (`{colors.dark-ink}` — #f2f0ee): Headlines and primary text on dark surfaces.
- **Dark Body** (`{colors.dark-body}` — #d8d6d3): Running text in dark mode.
- **Dark Muted** (`{colors.dark-muted}` — #9a9795): Secondary labels in dark mode.
- **Dark Muted Soft** (`{colors.dark-muted-soft}` — #767370): Tertiary text in dark mode.
- **Dark Alarm Red** (`{colors.dark-primary}` — #e85a4a): Alarm surface and primary actions in dark mode. Lighter than the light-mode red for perceptual balance against dark surfaces.

### Named Rules
**The One Alarm Color Rule.** Warm red is the only color that screams. Use it for the alarm screen, stop-alarm actions, and primary CTAs. Never use it for decoration, badges, or inactive states.

**The Pure Floor Rule.** The background is pure white in light mode and near-black in dark mode. Warmth lives in the brand colors and typography, not in a tinted surface.

## 3. Typography

**Font Family:** Inter, with a platform-native fallback stack (`-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif`).

**Character:** A single, friendly humanist sans for every role. No decorative display face. The system relies on weight and size contrast, not font pairing, to create hierarchy. This keeps the app feeling like a trustworthy native tool.

### Hierarchy
- **Display** (600, 36px, 1.15 line-height, -0.02em tracking): Full-screen alarm headline. The largest type in the app, used sparingly.
- **Headline** (600, 24px, 1.25 line-height, -0.01em tracking): Screen titles in top app bars.
- **Title** (600, 18px, 1.35 line-height): Card titles, section headers, setting group labels.
- **Body** (400, 16px, 1.5 line-height): Default running text, descriptions, toggle labels.
- **Label** (500, 14px, 1.25 line-height, 0.01em tracking): Buttons, navigation labels, list item titles.
- **Caption** (500, 12px, 1.35 line-height, 0.02em tracking): Badges, timestamps, helper text.

### Named Rules
**The One Voice Rule.** Inter carries every text role. Weight and size create hierarchy; font switching creates noise.

**The 56dp Alarm Rule.** The alarm screen uses Display at a size that is readable from arm's length in a dark room. Nothing else competes with it.

## 4. Elevation

The system is flat by default. Depth is communicated through tonal layering (`bg` → `surface` → `surface-elevated`) rather than shadows. This keeps the interface clean and avoids the visual noise of drop shadows on small screens.

Shadows are used only as a subtle response to state: a very soft shadow appears under the bottom navigation bar and under modal surfaces to separate them from content. Elevation is not a decorative device.

### Shadow Vocabulary
- **Navigation Shadow** (`0 -1px 3px rgba(28, 25, 23, 0.06)`): Separates the bottom navigation bar from scrolling content.
- **Modal Shadow** (`0 8px 24px rgba(28, 25, 23, 0.12)`): Gives dialogs and bottom sheets a clean lift above the page.

### Named Rules
**The Tonal-First Rule.** Surfaces are flat at rest. If you think you need a shadow, try a surface color change first.

## 5. Components

Components are rounded, generously padded, and built for thumbs. The vocabulary is intentionally small: buttons, cards, switches, inputs, badges, and navigation.

### Buttons
- **Shape:** 8px radius (`{rounded.md}`), 56px minimum height for primary actions, 48px for secondary.
- **Primary:** Filled `{colors.primary}` background, white text (`{colors.on-primary}`), 16px horizontal padding, 14px/500 label typography.
- **Pressed:** Background shifts to `{colors.primary-active}`. No transform or shadow change.
- **Secondary:** `{colors.surface}` background, `{colors.ink}` text, 1px `{colors.hairline}` border. Used for lower-emphasis actions.
- **Ghost:** Transparent background, `{colors.accent}` text. Used for inline links and "check again" actions.

### Cards
- **Corner Style:** 12px radius (`{rounded.lg}`).
- **Background:** `{colors.surface}` in light mode, `{colors.dark-surface}` in dark mode.
- **Border:** None by default. Optional 1px `{colors.hairline}` when a card sits on a surface of the same color.
- **Internal Padding:** 24px (`{spacing.xxl}`).
- **Shadow Strategy:** None at rest. Flat tonal separation from the background.

### Inputs / Fields
- **Style:** `{colors.bg}` background, 1px `{colors.hairline}` border, 8px radius, 12px × 16px internal padding, 48px height.
- **Focus:** Border shifts to `{colors.accent}`, with a 2px accent-colored outer ring at 20% opacity.
- **Error:** Border shifts to `{colors.error}`; error text uses `{colors.error}` at body size.

### Switches
- **Track (unchecked):** `{colors.hairline}` background.
- **Track (checked):** `{colors.primary}` background.
- **Thumb:** White, with a subtle shadow.
- **Size:** 52dp wide, 32dp tall. Large enough to hit accurately when groggy.

### Badges
- **Low Glucose:** `{colors.error}` fill, white text, 4px/10px padding, 4px radius.
- **High Glucose:** `{colors.warning}` fill, `{colors.ink}` text (dark text on amber for readability), same padding and radius.

### Navigation
- **Style:** Bottom navigation bar, `{colors.surface}` background with a hairline top border.
- **Typography:** `{typography.label}`, 14px/500.
- **Default:** `{colors.muted}` icon and label.
- **Active:** `{colors.primary}` icon and label, no background indicator.
- **Mobile:** Stays fixed at the bottom; no hamburger or top tabs.

### Signature Component: Alarm Surface
- **Background:** `{colors.primary}` full-bleed, edge-to-edge.
- **Headline:** `{typography.display}` in white, centered.
- **Primary Action:** Full-width 56dp button with `{colors.bg}` fill and `{colors.primary}` text — the largest, most obvious target.
- **Secondary Actions:** Three equal-width outlined buttons for snooze durations (30 min / 1 h / 3 h), white text on semi-transparent white fill.
- **Icon:** A single 48dp warning icon in white, above the headline.
- **Behavior:** No scroll, no nav bar, no status-bar distractions. The entire screen is one message and one primary action.

## 6. Do's and Don'ts

### Do:
- **Do** use `{colors.primary}` only for alarms, stop-alarm actions, and primary CTAs. Its scarcity is the point.
- **Do** keep every touch target at or above 48dp; alarm actions are 56dp.
- **Do** use Inter for every text role. Weight and size create hierarchy.
- **Do** respect dark mode as a first-class surface. The alarm screen still uses warm red; text and cards shift to warm-tinted dark neutrals.
- **Do** use color-independent indicators alongside color: a warning icon with a low badge, text labels with status dots.
- **Do** keep the alarm screen minimal: one headline, one dismiss button, three snooze options. Nothing else.

### Don't:
- **Don't** use cream, sand, beige, or warm-tinted backgrounds. The floor is pure white in light mode and near-black in dark mode. Warmth comes from the alarm red, not the canvas.
- **Don't** use a separate display font or decorative serif. PRODUCT.md rejects "AI/SaaS marketing tropes" like slab-serif editorial type; this app is a tool, not a magazine.
- **Don't** use coral, purple gradients, neon accents, glassmorphism, or gradient text. The existing code copied these from Claude.com; they are prohibited here.
- **Don't** use heavy shadows or card grids as decoration. Cards group information; they are not a visual motif.
- **Don't** put a tiny uppercase eyebrow above every section. One screen, one clear heading.
- **Don't** use side-stripe borders greater than 1px as colored accents on cards or alerts.
- **Don't** rely on color alone to distinguish high from low glucose. Pair the badge color with a text label and, where possible, an icon.
