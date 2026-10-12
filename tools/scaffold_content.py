"""Reproducible placeholder catalogue and original technical audio. No exercise instructions."""
import json
import math
from pathlib import Path
import struct
import wave
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / 'app/src/main/assets'
ASSETS.mkdir(parents=True, exist_ok=True)
titles = [
    ('chair-introduction', 'A gentle seated start', 5, 'SEATED', 'chair'),
    ('chair-everyday', 'Your seated everyday routine', 10, 'SEATED', 'chair'),
    ('chair-unhurried', 'An unhurried seated session', 15, 'SEATED', 'chair'),
    ('mobility-short', 'A little time for mobility', 5, 'SEATED', 'mobility'),
    ('mobility-everyday', 'Everyday mobility', 10, 'MIXED', 'mobility'),
    ('mobility-long', 'Space to move gently', 15, 'MIXED', 'mobility'),
    ('balance-introduction', 'Getting started with balance', 5, 'STANDING', 'balance'),
    ('balance-supported', 'Supported beginner balance', 10, 'STANDING', 'balance'),
    ('balance-unhurried', 'Balance at your own pace', 15, 'STANDING', 'balance'),
    ('chair-week-one', 'Week one: a comfortable beginning', 10, 'SEATED', 'chair'),
]
workouts = []
for id, title, minutes, posture, category in titles:
    workouts.append(dict(id=id, title=title, description='Planning placeholder. Movement, safety, narration and media await qualified professional review.',
        durationMinutes=minutes, difficulty='Beginner (proposed)', posture=posture, category=category,
        language='en-GB', programmeId='seated-foundations' if id == 'chair-week-one' else None,
        review=dict(status='DRAFT', date=None, reviewer=None, credentials=None, evidenceId=None),
        contraindicationNotes=[], safetyPrompts=[], movements=[], videoUri=None, voices=[],
        licence=dict(reference='', offlineAllowed=False, expiresAt=None), placeholder=True))
catalogue = dict(schemaVersion=1, version=1, expiresAt=None, workouts=workouts, programmes=[
    dict(id='seated-foundations', title='Seated foundations', weeks=4, description='A proposed four-week programme. Sessions and weekly assignments await review.', workoutIds=['chair-week-one'], placeholder=True),
    dict(id='everyday-mobility', title='Everyday ease', weeks=4, description='A proposed four-week mobility programme. Content is being planned.', workoutIds=[], placeholder=True)])
(ASSETS / 'catalogue-v1.json').write_text(json.dumps(catalogue, indent=2) + '\n', encoding='utf-8')
debug_assets = ROOT / 'app/src/debug/assets'
debug_assets.mkdir(parents=True, exist_ok=True)
with wave.open(str(debug_assets / 'technical-demo.wav'), 'wb') as audio:
    audio.setparams((1, 2, 22050, 0, 'NONE', 'not compressed'))
    for n in range(12 * 22050):
        t = n / 22050
        # Quiet original test tone, fades at both ends; never presented as narration.
        amplitude = min(1, t, 12-t) * 500
        audio.writeframesraw(struct.pack('<h', int(amplitude * math.sin(2 * math.pi * 220 * t))))

strings = {
'app_name': 'LivingRoom Move', 'brand_note': 'A little movement. A little you-time.',
'tagline': 'Gentle movement. Right from your living room.', 'home': 'Home', 'sessions': 'Gentle sessions',
'programmes': 'My programmes', 'favourites': 'Favourites', 'progress': 'Progress', 'settings': 'Settings',
'remote_hint': 'Use the arrows to explore. Select to choose. Back to return.',
'greeting': 'WELCOME TO YOUR OWN PACE', 'hero_title': 'A softer start.\nA moment for you.',
'start_gentle': 'Start a gentle session', 'chair': 'Chair-supported movement', 'mobility': 'Mobility & flexibility',
'balance': 'Beginner balance', 'draft_notice': 'Preview catalogue • Sessions await professional review and licensed media.',
'reminder_message': 'If you feel like it, there is time for a gentle session. There is no schedule to keep.',
'empty_sessions': 'Nothing here yet. Try a different duration or position, or save a favourite from Gentle sessions.',
'clear_filters': 'Show all durations and positions', 'session_meta': '%1$s · %2$s',
'draft_badge': 'DRAFT · Awaiting review', 'approved_badge': 'Reviewed session',
'draft_detail': 'This session is a planning placeholder. It has no approved exercise instructions, narration or video yet. Playback is unavailable until a qualified exercise professional has reviewed it and media rights are recorded.',
'add_favourite': 'Save as a favourite', 'remove_favourite': 'Remove favourite', 'play_session': 'Play free session',
'back_sessions': 'Back to sessions', 'before_start': 'Before you begin',
'safety_text': 'Choose a clear, comfortable space. If using a chair, use a stable chair without wheels. Move at a pace that feels comfortable and stop if you feel unwell or uncomfortable. If you are unsure whether a session is suitable for you, seek advice from a qualified professional.\n\nThis general information needs professional review before release. We will remember your acknowledgement on this device.',
'safety_accept': 'I have read this · Continue', 'back': 'Back', 'test_complete': 'Playback test complete',
'session_complete': 'Time well spent', 'test_no_progress': 'Technical audio test only. No exercise instruction. This does not count towards workout progress.',
'completion_text': 'Your session has been saved on this device. Take your time before choosing what comes next.',
'replay': 'Replay from start', 'programme_intro': 'Four weeks, at your own pace. Programme content is not available yet.',
'programme_draft': 'PLANNING DRAFT · Not for sale', 'view_membership': 'Membership information', 'membership': 'More room to move',
'billing_unavailable': 'Membership is not available in this preview. You will not be charged. Free starter sessions will not require a subscription.',
'test_prices': 'Proposed UK test offers: £4.99 monthly or £29.99 annually. These are planning prices, not live purchase offers.',
'renewal_draft': 'Release disclosure draft: subscriptions renew automatically at the Amazon-displayed price until cancelled. Manage renewal through your Amazon account. Final terms, billing period and cancellation wording require review before sale.',
'restore': 'Restore Amazon purchases', 'restore_unavailable': 'Restoring purchases is not available in this preview. No payment or membership has been changed.',
'back_programmes': 'Back to programmes',
'empty_progress': 'Your completed sessions will appear here. You can return whenever it suits you. Drafts and playback tests are not counted.',
'family_setup': 'Family setup', 'family_active': 'With permission, help choose comfortable settings. Changes are saved on this television. There is no remote monitoring.',
'text_size': 'Text size: +%1$d%%', 'captions_setting': 'Captions: %1$s', 'speed_setting': 'Narration and video speed: %1$.2f×',
'motion_setting': 'Reduced motion: %1$s', 'posture_setting': 'Preferred position: %1$s', 'any_position': 'Any position',
'duration_setting': 'Preferred duration: %1$s', 'any_duration': 'Any duration',
'reminder_setting': 'Gentle welcome reminder: %1$s', 'language_setting': 'Language: %1$s',
'language_drafts': 'French, German, Spanish, Italian, Portuguese and Dutch are prepared for translation review. English is used until reviewed translations are available.',
'delete_data': 'Delete local data', 'technical_test': 'Developer playback test',
'privacy_summary': 'Your preferences, favourites and progress stay on this device. No diagnoses, tracking, analytics uploads or accounts are collected. Delete local data whenever you choose.',
'finish_family': 'Finish family setup', 'family_consent': 'Would you like someone you trust to help with settings? Continue only with your permission. They can adjust text, captions, speed and preferences here. Nothing is shared and nobody can monitor you remotely.',
'family_agree': 'Yes, help me with my settings', 'delete_confirmation': 'This removes preferences, favourites, progress and your safety acknowledgement from this device. Amazon purchases are managed separately.',
'keep_data': 'Keep my data', 'delete_confirm': 'Delete data on this device', 'on': 'On', 'off': 'Off',
'seated': 'Seated', 'standing': 'Standing', 'mixed': 'Seated & standing', 'playback_time': '%1$d of %2$d seconds',
'buffering': 'Preparing playback…', 'playback_error': 'Playback was interrupted. Check your connection or try again. Your place is saved; the session has not been marked complete.',
'retry': 'Try playback again', 'pause': 'Pause', 'resume': 'Resume', 'narration_setting': 'Narration: %1$s',
}
def write_strings(path, values):
    resources = ET.Element('resources')
    for name, value in values.items():
        ET.SubElement(resources, 'string', {'name': name}).text = value
    for name, forms in {'minutes': {'one': '%1$d minute', 'other': '%1$d minutes'},
                         'progress_total': {'one': '%1$d completed session', 'other': '%1$d completed sessions'}}.items():
        plural = ET.SubElement(resources, 'plurals', {'name': name})
        for quantity, text in forms.items():
            ET.SubElement(plural, 'item', {'quantity': quantity}).text = text
    ET.indent(resources, space='    ')
    path.parent.mkdir(parents=True, exist_ok=True)
    ET.ElementTree(resources).write(path, encoding='utf-8', xml_declaration=True)
write_strings(ROOT / 'app/src/main/res/values/strings.xml', strings)
write_strings(ROOT / 'app/src/main/res/values-en-rGB/strings.xml', strings)
us = dict(strings)
us.update(favourites='Favorites', programmes='My programs', add_favourite='Save as a favorite', remove_favourite='Remove favorite')
write_strings(ROOT / 'app/src/main/res/values-en-rUS/strings.xml', us)
for locale in ['fr', 'de', 'es', 'it', 'pt', 'nl']:
    folder = ROOT / 'localisation/drafts' / locale
    folder.mkdir(parents=True, exist_ok=True)
    (folder / 'review.json').write_text(json.dumps(dict(locale=locale, status='NOT_TRANSLATED', humanReviewRequired=True, approved=False), indent=2), encoding='utf-8')
print('Created ten DRAFT sessions, English resources, six translation queues and original technical test audio.')
