import {AbsoluteFill, Audio, staticFile, useCurrentFrame, useVideoConfig} from 'remotion';
import sessions from './sessions.json';
import speechCues from './speech-cues.json';
import type {Caption} from '@remotion/captions';

export type SessionProps = {sessionId: string; cardOnly: boolean};
const palette = {cream:'#f5f1e8', green:'#294c3b', ink:'#253b30', sage:'#d5dfc9', gold:'#c79b5c'};
const formatTime = (seconds:number) => `${Math.floor(seconds/60).toString().padStart(2,'0')}:${Math.floor(seconds%60).toString().padStart(2,'0')}`;

/** One intentionally data-driven template governs all review chapters. */
export const SessionFilm = ({sessionId, cardOnly}:SessionProps) => {
  const frame = useCurrentFrame();
  const {fps} = useVideoConfig();
  const session = sessions.find(s => s.id === sessionId);
  if(!session) throw new Error(`Unknown session ${sessionId}`);
  const second = frame/fps;
  const index = session.chapters.findIndex(c => second >= c.start && second < c.start + c.seconds);
  const chapter = session.chapters[Math.max(0,index)];
  const next = session.chapters[index+1];
  const remaining = Math.max(0,chapter.start+chapter.seconds-second);
  const cueData = speechCues as Record<string, {start:number; end:number; text:string}[]>;
  const activeCue = cueData[sessionId]?.find(c => second >= c.start && second < c.end);
  const caption:Caption|null = activeCue ? {text:activeCue.text,startMs:activeCue.start*1000,
    endMs:activeCue.end*1000,timestampMs:null,confidence:null} : null;
  return <AbsoluteFill style={{background:palette.cream,color:palette.ink,fontFamily:'Segoe UI, Arial, sans-serif'}}>
    {!cardOnly && <Audio src={staticFile(`audio/${sessionId}.wav`)} />}
    <div style={{position:'absolute',left:0,top:0,width:1180,height:1080,
      background:'radial-gradient(ellipse at 20% 80%, #e0e6d8 0%, #f5f1e8 65%)'}} />
    <div style={{position:'absolute',right:0,top:0,width:740,height:1080,background:palette.green}} />
    <div style={{position:'absolute',left:90,top:80,fontSize:32,letterSpacing:3,fontWeight:600}}>LIVINGROOM MOVE <span style={{color:'#6b795f',fontWeight:400}}> / YOUR OWN PACE</span></div>
    <div style={{position:'absolute',left:90,top:175,fontSize:27,letterSpacing:3,color:'#67705d'}}>{session.posture} · {session.durationSeconds/60} MINUTES · PRODUCTION REVIEW</div>
    <div style={{position:'absolute',left:85,top:238,width:1010,fontFamily:'Georgia, serif',fontSize:92,lineHeight:1.06,letterSpacing:-2}}>{chapter.title}</div>
    <div style={{position:'absolute',left:90,top:505,width:910,fontSize:45,lineHeight:1.35,fontWeight:400}}>{chapter.cue}</div>
    <div style={{position:'absolute',left:90,top:675,width:945,borderTop:'2px solid #b9c3af',paddingTop:28,fontSize:27,lineHeight:1.5,color:'#53624e'}}>
      <span style={{fontWeight:600,letterSpacing:2}}>FILMING NOTE</span><br/>
      {chapter.kind === 'movement' ? 'Approved instructor demonstration required. The timer is an editorial window, not a continuous repetition target.' : 'A quiet transition, with no pressure to keep up. The final footage and script need professional approval.'}
    </div>
    <div style={{position:'absolute',right:100,top:92,width:540,fontSize:29,color:palette.sage,lineHeight:1.4}}>{session.title}</div>
    <div style={{position:'absolute',left:1320,top:279,fontSize:26,color:palette.sage,letterSpacing:3}}>CHAPTER {Math.max(0,index)+1} / {session.chapters.length}</div>
    {!cardOnly && <div style={{position:'absolute',left:1320,top:355,fontSize:130,color:palette.cream,fontVariantNumeric:'tabular-nums'}}>{formatTime(Math.ceil(remaining))}</div>}
    <div style={{position:'absolute',left:1320,top:550,fontSize:26,color:palette.gold,letterSpacing:2}}>UP NEXT</div>
    <div style={{position:'absolute',left:1320,top:605,width:470,fontSize:43,color:palette.cream,lineHeight:1.25,fontFamily:'Georgia, serif'}}>{next?.title ?? 'Your session closes'}</div>
    <div style={{position:'absolute',left:1320,top:810,width:440,height:5,background:'#64816a'}}>
      <div style={{height:'100%',width:`${cardOnly?chapter.start/session.durationSeconds*100:second/session.durationSeconds*100}%`,background:palette.gold}} />
    </div>
    <div style={{position:'absolute',left:1320,top:839,fontSize:22,color:palette.sage}}>A LITTLE TIME. AT YOUR OWN PACE.</div>
    {!cardOnly && caption && <div style={{position:'absolute',left:90,bottom:120,width:970,
      background:palette.green,color:palette.cream,fontSize:26,lineHeight:1.35,padding:'12px 18px',borderRadius:6}}>{caption.text}</div>}
    <div style={{position:'absolute',left:0,bottom:0,width:'100%',height:100,background:'#eadfcd',display:'flex',alignItems:'center',paddingLeft:90,gap:28,fontSize:26,color:'#60442d'}}>
      <span style={{padding:'10px 20px',background:'#60442d',color:'#fff8ec',borderRadius:4,fontSize:23,fontWeight:700,letterSpacing:2}}>DRAFT</span>
      UNREVIEWED · NOT FOR EXERCISE USE · SYNTHETIC REVIEW VOICE
    </div>
  </AbsoluteFill>;
};
