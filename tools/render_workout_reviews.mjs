// Render authored Remotion cards once, then encode full-duration review timelines.
import {bundle} from '../workout-studio/node_modules/@remotion/bundler/dist/index.js';
import {renderStill, selectComposition} from '../workout-studio/node_modules/@remotion/renderer/dist/index.js';
import {readFile, mkdir, writeFile, copyFile} from 'node:fs/promises';
import path from 'node:path';
import {spawnSync} from 'node:child_process';
const root = path.resolve(import.meta.dirname, '..');
const sessions = JSON.parse(await readFile(path.join(root, 'content/drafts/index.json'), 'utf8'));
const out = path.join(root, 'workout-studio/out');
await mkdir(out, {recursive:true});
const ffmpegResult = spawnSync('python', ['-c', 'import imageio_ffmpeg; print(imageio_ffmpeg.get_ffmpeg_exe())'], {encoding:'utf8'});
if(ffmpegResult.status !== 0) throw new Error(ffmpegResult.stderr);
const ffmpeg = ffmpegResult.stdout.trim();
const serveUrl = await bundle({entryPoint:path.join(root, 'workout-studio/src/index.ts'), publicDir:path.join(root, 'workout-studio/public')});
function run(args) {
  const result = spawnSync(ffmpeg, ['-hide_banner', '-loglevel', 'error', '-y', ...args], {encoding:'utf8'});
  if(result.status !== 0) throw new Error(result.stderr);
}
for(const session of sessions) {
  const folder = path.join(out, session.id);
  await mkdir(folder, {recursive:true});
  const clips = [];
  for(let i=0;i<session.chapters.length;i++) {
    const chapter = session.chapters[i];
    const image = path.join(folder, `chapter-${String(i).padStart(2,'0')}.png`);
    const props = {sessionId:session.id, cardOnly:true};
    const composition = await selectComposition({serveUrl, id:session.id, inputProps:props});
    await renderStill({serveUrl, composition, inputProps:props, output:image,
      frame:(chapter.start + 1)*30, imageFormat:'png', logLevel:'error'});
    const clip = path.join(folder, `chapter-${String(i).padStart(2,'0')}.mp4`);
    const filterFile = path.join(folder, 'timer-filter.txt');
    // Windows filter paths need forward slashes and an escaped drive colon.
    const font = 'C\\:/Windows/Fonts/segoeui.ttf';
    const timer = `drawtext=fontfile='${font}':text='%{eif\\:floor(max(0\\,ceil(${chapter.seconds}-t))/60)\\:d\\:2}\\:%{eif\\:mod(max(0\\,ceil(${chapter.seconds}-t))\\,60)\\:d\\:2}':x=1320:y=355:fontsize=130:fontcolor=0xf5f1e8`;
    // Cue-card frames change once a second; the interactive studio stays 30fps.
    // Render review video at 1fps to avoid encoding identical frames 30 times.
    await writeFile(filterFile, `fps=1,${timer},format=yuv420p`);
    run(['-loop','1','-framerate','1','-i',image,'-t',String(chapter.seconds),
      '-filter_script:v',filterFile,'-c:v','libx264','-preset','ultrafast','-crf','26',
      '-threads','2','-g','150','-an',clip]);
    clips.push(clip);
  }
  const concatFile = path.join(folder, 'clips.txt');
  await writeFile(concatFile, clips.map(p=>`file '${p.replaceAll('\\','/')}'`).join('\n'));
  const silent = path.join(folder, 'silent-review.mp4');
  run(['-f','concat','-safe','0','-i',concatFile,'-c','copy','-movflags','+faststart',silent]);
  const narrated = path.join(folder, 'narrated-review.mp4');
  run(['-i',silent,'-i',path.join(root, `workout-studio/public/audio/${session.id}.wav`),
    '-map','0:v:0','-map','1:a:0','-c:v','copy','-c:a','aac','-b:a','96k','-t',String(session.durationSeconds),
    '-movflags','+faststart',narrated]);
  await copyFile(path.join(root, `content/drafts/${session.id}/captions-en-GB.vtt`),path.join(folder,'captions-en-GB.vtt'));
  console.log(`Rendered ${session.id}: ${session.durationSeconds}s, 1080p cue-card review; UNREVIEWED`);
}
