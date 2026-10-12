import {Composition} from 'remotion';
import {SessionFilm} from './Composition';

export const RemotionRoot = () => <>
  <Composition id="chair-introduction" component={SessionFilm} width={1920} height={1080} fps={30} durationInFrames={9000} defaultProps={{sessionId:'chair-introduction',cardOnly:false}} />
  <Composition id="chair-everyday" component={SessionFilm} width={1920} height={1080} fps={30} durationInFrames={18000} defaultProps={{sessionId:'chair-everyday',cardOnly:false}} />
  <Composition id="chair-unhurried" component={SessionFilm} width={1920} height={1080} fps={30} durationInFrames={27000} defaultProps={{sessionId:'chair-unhurried',cardOnly:false}} />
  <Composition id="mobility-short" component={SessionFilm} width={1920} height={1080} fps={30} durationInFrames={9000} defaultProps={{sessionId:'mobility-short',cardOnly:false}} />
  <Composition id="mobility-everyday" component={SessionFilm} width={1920} height={1080} fps={30} durationInFrames={18000} defaultProps={{sessionId:'mobility-everyday',cardOnly:false}} />
  <Composition id="mobility-long" component={SessionFilm} width={1920} height={1080} fps={30} durationInFrames={27000} defaultProps={{sessionId:'mobility-long',cardOnly:false}} />
  <Composition id="balance-introduction" component={SessionFilm} width={1920} height={1080} fps={30} durationInFrames={9000} defaultProps={{sessionId:'balance-introduction',cardOnly:false}} />
  <Composition id="balance-supported" component={SessionFilm} width={1920} height={1080} fps={30} durationInFrames={18000} defaultProps={{sessionId:'balance-supported',cardOnly:false}} />
  <Composition id="balance-unhurried" component={SessionFilm} width={1920} height={1080} fps={30} durationInFrames={27000} defaultProps={{sessionId:'balance-unhurried',cardOnly:false}} />
  <Composition id="chair-week-one" component={SessionFilm} width={1920} height={1080} fps={30} durationInFrames={18000} defaultProps={{sessionId:'chair-week-one',cardOnly:false}} />
</>;
