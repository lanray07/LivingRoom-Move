param([string]$Workspace = (Split-Path -Parent $PSScriptRoot))
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Speech
$narrator = New-Object System.Speech.Synthesis.SpeechSynthesizer
try {
    $narrator.SelectVoice('Microsoft Hazel Desktop')
    $narrator.Rate = -1
    $sessions = Get-Content -LiteralPath (Join-Path $Workspace 'content/drafts/index.json') -Raw | ConvertFrom-Json
    foreach ($session in $sessions) {
        $folder = Join-Path $Workspace ('artifacts/review-narration/' + $session.id)
        New-Item -ItemType Directory -Force -Path $folder | Out-Null
        $index = 0
        foreach ($chapter in $session.chapters) {
            $sentenceIndex = 0
            foreach ($sentence in [regex]::Split($chapter.script, '(?<=[.!?])\s+')) {
                if ([string]::IsNullOrWhiteSpace($sentence)) { continue }
                $path = Join-Path $folder ('{0:D2}-{1:D2}.wav' -f $index, $sentenceIndex)
                $narrator.SetOutputToWaveFile($path)
                $narrator.Speak($sentence)
                $narrator.SetOutputToNull()
                $sentenceIndex++
            }
            $index++
        }
        Write-Output ('Synthesised review speech: ' + $session.id)
    }
} finally { $narrator.Dispose() }
